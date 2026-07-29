<h1 style="text-align: center;">Claustrum</h1>

A privacy focused robust password manager. I created Claustrum to securely and safely store my password even in plain text. I do not trust online password managers as it is online and NOT SECURE however much one can guarantee that it is safe & such. As the growing rate of loss of privacy, Wouldn't it better to have a cross-platform password-manager?

## Overview
Claustrum stores your saved credentials (title + password pairs) in a single encrypted file on disk, unlocked with one master password. Nothing is stored in plaintext except salt on disk, all passwords are encrypted at rest using AES-GCM, with the encryption key derived from your master password via PBKDF2.

## Features
- **Master-password gated:** All the stored passkey and credentials are unlocked by one master-password entry. (One wrong entry loudly crashes the application)
- **AES-256 GCM encryption:** Claustrum uses Aes-256 with GCM generated from masterkey, salt stored to encrypt all the credentials.
- **Material-You (M3) Theme:** Claustrum currently tries to follow the Material you (M3) design layout of pill shaped button, search bar and such.

## Setup / Instructions
If you want to commit and update the project and or to keep it going, fork it, star it and commit if possible.

Here's how you can clone the project
```
git --clone https://github.com/blackoutxo/Claustrum
```

For running the application, one must have JDK 17 installed. Once installed, run it from CMD with 'java -jar Claustrum.jar'. The opened application asks to set for password which the application doesn't ask when the user actually starts storing passkeys

