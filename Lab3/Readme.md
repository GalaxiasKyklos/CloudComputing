# Face Recognition

## Saúl Ponce

## is699399

### What works

- Everything works

### Technical report
- `ASN_HW3` has this line, which loads the `opencv` library into the project `System.load("C:\\opencv34\\opencv\\build\\java\\x64\\opencv_java341.dll");`
- `S3` is a utility class with static methods to put and get all objects form S3, I had to put my AWS credentials beacouse the AWS SDK did not find them in the `C:\\.aws` directory (I did this Lab on Windows beacouse I had trouble getting OpenCV to work on macOS), so I created a `ConfigSecretet` file to put the credentials.
- `FaceAddFrame` implemented:
    - `startButtonActionPerformed`
    - `stopButtonActionPerformed`
    - `uploadButtonActionPerformed` this one uses the S3 utility class
- `FaceAuthFrame` implemented:
    - `doAuthLogic` Uses `AWSFaceCompare` to use AWS Rekognition
- `AWSFaceCompare` has a `compare` method, it uses the S3 utitlity class to list the S3 objects and check if there is a match using AWS Rekognition
