# Face Recognition

## Saúl Ponce

## is699399

### What works

- Everything works

### Technical report
- `ASN_HW3` has this line, which loads the `opencv` library into the project `System.load("C:\\opencv34\\opencv\\build\\java\\x64\\opencv_java341.dll");`
- `S3` is a utility class with static methods to put and get all objects form S3
- `FaceAddFrame` implemented:
    - `startButtonActionPerformed`
    - `stopButtonActionPerformed`
    - `uploadButtonActionPerformed` this one uses the S3 utility class
- `FaceAuthFrame` implemented:
    - `doAuthLogic` Uses `AWSFaceCompare` to use AWS Rekognition
- `AWSFaceCompare` has a `compare` methos, it uses the S3 utitlity class to list the S3 objects and check if there is a match using AWS Rekognition
