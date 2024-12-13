# <img src="https://github.com/user-attachments/assets/1b053ed2-ac24-4e63-8db1-3a77b2a4be33" width="60" height="60" align="center" /> Cat or Dog Classifier

Car or Dog Classifier is a mobile application that uses machine learning to classify images as either a cat or a dog. It leverages a TensorFlow-based convolutional neural network (CNN) model to provide accurate predictions along with confidence scores.

## ⭐️Features
- **Image Classification:** Capture a photo to determine whether it show a cat or a dog.
  
- **Confidence Score:** Displays the model’s confidence percentage for the prediction.

## ⚙️Technologies
### 📱App:
- **Jetpack compose:** Separating the project into layers with repositories and uses cases, using view model functionalities.🔧

- **CameraX:** For camera UI

### 🌐Model:
- **TensorFlow:** For building and training the CNN model.

- **NumPy & Pandas:** For data handling.

- **Matplotlib:** For visualizing model performance.

## ML model
1. Input Layer:
Resizes images to 128x128 pixels.
2. Convolutional Layers
3. MaxPooling Layers
4. Flatten Layer
5. Dense Layer
6. Output Layer

## Dataset
- Training Data:
  - 8,000 labeled images (cats and dogs).
- Test Data:
  - 2,000 labeled images.
- Preprocessing:
  - Data Augmentation:
  - Scaling, shearing, zooming, and flipping applied to training images.
- Rescaling:
  - Normalized pixel values between 0 and 1 for test images.
  
## Installation

1. Clone the repository:
```bash
git clone https://github.com/AdamDawi/Cat-Or-Dog-Classifier
```
2. Open the project in Android Studio.
3. Be sure the versions in gradle are same as on github

## Here are some overview pictures:
![11](https://github.com/user-attachments/assets/6a9a5e7b-714b-4b11-8c29-de991e580d24)
![22](https://github.com/user-attachments/assets/6ca61a55-ffbc-44b2-a6e0-472fe682d540)

## Requirements
Minimum version: Android 7.0 (API level 24) or later📱

Target version: Android 14 (API level 34) or later📱

## Author

Adam Dawidziuk🧑‍💻
