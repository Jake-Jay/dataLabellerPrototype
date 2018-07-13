Data Labelling Prototype
Android Application intended to help label image data. This is currently only useful for classification problems. The API designed to work with this app can be found here.

Instructions to clone project:
Option 1

Open android studio and select 'Check out project from Version Control'
Select GitHub from the dropdown list.
Enter your GitHub credentials. Then click Login
Fill out the Clone Repository form and click Clone
Open the project
Once the application is open click the button in the top right corner that says "sync project with gradle files"
Option 2 (if Android Studio is already open)

Select VCS > Git > Clone
Android Studio will promt for the new application to be opened
Once the application is open click the button in the top right corner that says "sync project with gradle files"
Running the Project
The application should run on all modern Android API versions. Simply click run and choose your device.

Connect to the server

The application is currenlty set up to work on a local network. It acesses a server at http://10.20.20.101:5000/

To change this the BASE_URL needs to be changed in the following files:

NetworkUtils.java
AddLabel.java
RegisterUser.java