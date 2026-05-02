package utils;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.Base64;

import nu.pattern.OpenCV;

public class SecurityUtil {

    // Initialize OpenCV native libraries
    static {
        OpenCV.loadLocally();
    }

    /**
     * Executes the PowerShell script to find a removable USB drive and generate a hardware signature.
     */
    public static String detectUsbHardwareKey() throws Exception {
        String psScript = 
            "try {\n" +
            "    $usbDrives = Get-WmiObject -Class Win32_LogicalDisk | Where-Object { $_.DriveType -eq 2 }\n" +
            "    if ($usbDrives.Count -eq 0) { Write-Output 'NO_USB_FOUND'; exit 1 }\n" +
            "    $drive = $usbDrives[0]\n" +
            "    $driveLetter = $drive.DeviceID\n" +
            "    $volume = Get-WmiObject -Class Win32_Volume | Where-Object { $_.DriveLetter -eq $driveLetter } | Select-Object -First 1\n" +
            "    $signature = @()\n" +
            "    if ($volume.SerialNumber) { $signature += \\\"VSN:$($volume.SerialNumber)\\\" } elseif ($drive.VolumeSerialNumber) { $signature += \\\"VSN:$($drive.VolumeSerialNumber)\\\" }\n" +
            "    $signature += \\\"DRV:$driveLetter\\\"\n" +
            "    if ($volume.Label) { $signature += \\\"LBL:$($volume.Label)\\\" } elseif ($drive.VolumeName) { $signature += \\\"LBL:$($drive.VolumeName)\\\" }\n" +
            "    if ($volume.FileSystem) { $signature += \\\"FS:$($volume.FileSystem)\\\" }\n" +
            "    if ($drive.Size) { $capacityMB = [Math]::Round($drive.Size / 1MB); $signature += \\\"CAP:${capacityMB}MB\\\" }\n" +
            "    $diskDrive = Get-WmiObject -Query \\\"SELECT * FROM Win32_DiskDrive WHERE InterfaceType='USB'\\\" | Select-Object -First 1\n" +
            "    if ($diskDrive) {\n" +
            "        if ($diskDrive.SerialNumber) { $signature += \\\"DSN:$($diskDrive.SerialNumber)\\\" }\n" +
            "        if ($diskDrive.Model) { $modelClean = $diskDrive.Model -replace '\\s+', '_'; $signature += \\\"MDL:$modelClean\\\" }\n" +
            "    }\n" +
            "    $hardwareKey = $signature -join '|'\n" +
            "    if ([string]::IsNullOrWhiteSpace($hardwareKey)) { Write-Output 'INVALID_USB_DATA'; exit 1 }\n" +
            "    Write-Output $hardwareKey\n" +
            "    exit 0\n" +
            "} catch { Write-Output 'USB_DETECTION_ERROR'; exit 1 }";

        File tempScript = File.createTempFile("detect_usb", ".ps1");
        Files.writeString(tempScript.toPath(), psScript);

        ProcessBuilder pb = new ProcessBuilder("powershell.exe", "-ExecutionPolicy", "Bypass", "-File", tempScript.getAbsolutePath());
        Process p = pb.start();
        p.waitFor();
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = reader.readLine();
        tempScript.delete();

        if (line != null && !line.isEmpty() && !line.contains("NO_USB_FOUND") && !line.contains("ERROR")) {
            return line.trim();
        }
        throw new Exception("No USB Key found. Please insert a removable drive.");
    }

    /**
     * Opens the webcam, detects a face, and generates a Base64 signature of the cropped face.
     * In a full production system, this would extract 128D embeddings. 
     * For this implementation, we extract the cropped face hash to serve as the biometric signature.
     */
    public static String captureFaceSignature() throws Exception {
        VideoCapture camera = new VideoCapture(0);
        if (!camera.isOpened()) {
            throw new Exception("Could not open webcam.");
        }

        // Load cascade classifier
        String cascadePath = SecurityUtil.class.getResource("/haarcascade_frontalface_alt.xml").getPath();
        // Handle Windows path leading slash issue
        if(cascadePath.startsWith("/")) {
            cascadePath = cascadePath.substring(1);
        }
        CascadeClassifier faceDetector = new CascadeClassifier(cascadePath);
        if (faceDetector.empty()) {
            camera.release();
            throw new Exception("Failed to load face detection model.");
        }

        Mat frame = new Mat();
        long startTime = System.currentTimeMillis();
        String faceSignature = null;

        // Try capturing for up to 5 seconds
        while (System.currentTimeMillis() - startTime < 5000) {
            if (camera.read(frame)) {
                Mat grayFrame = new Mat();
                Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
                Imgproc.equalizeHist(grayFrame, grayFrame);

                MatOfRect faceDetections = new MatOfRect();
                faceDetector.detectMultiScale(grayFrame, faceDetections, 1.1, 3, 0, new Size(100, 100), new Size());

                Rect[] facesArray = faceDetections.toArray();
                if (facesArray.length > 0) {
                    // Get the first face detected
                    Rect face = facesArray[0];
                    Mat croppedFace = new Mat(grayFrame, face);
                    
                    // Resize to a standard size for consistent hashing
                    Mat resizedFace = new Mat();
                    Imgproc.resize(croppedFace, resizedFace, new Size(160, 160));

                    // Convert to bytes
                    MatOfByte buffer = new MatOfByte();
                    Imgcodecs.imencode(".jpg", resizedFace, buffer);
                    byte[] faceBytes = buffer.toArray();

                    // Hash the bytes to create a signature
                    MessageDigest digest = MessageDigest.getInstance("SHA-256");
                    byte[] hash = digest.digest(faceBytes);
                    faceSignature = Base64.getEncoder().encodeToString(hash);
                    
                    break; // Face found and processed
                }
            }
        }
        
        camera.release();

        if (faceSignature == null) {
            throw new Exception("No face detected in front of the camera.");
        }
        
        return "FACE-ID:" + faceSignature;
    }
}
