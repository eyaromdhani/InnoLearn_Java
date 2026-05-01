import speech_recognition as sr
import pyautogui
import pyperclip
import time
import sys

def listen_and_type():
    recognizer = sr.Recognizer()

    with sr.Microphone() as source:
        print("Écoute en cours... (Parlez maintenant)")
        # Ajuster le bruit ambiant
        recognizer.adjust_for_ambient_noise(source, duration=0.5)
        try:
            audio = recognizer.listen(source, timeout=5, phrase_time_limit=5)
            print("Traitement...")

            # Reconnaissance via Google (Français)
            text = recognizer.recognize_google(audio, language="fr-FR")
            print(f"Texte détecté : {text}")

            # Copier le texte dans le presse-papiers (gère mieux les accents)
            pyperclip.copy(text)

            # Petit délai pour laisser Java reprendre le focus
            time.sleep(0.3)

            # Sélectionner tout et coller
            pyautogui.hotkey('ctrl', 'a')
            pyautogui.press('backspace')
            pyautogui.hotkey('ctrl', 'v')

            # Valider
            pyautogui.press('enter')

        except Exception as e:
            print(f"Erreur : {e}")

if __name__ == "__main__":
    listen_and_type()
