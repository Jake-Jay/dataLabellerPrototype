# Data Labelling Prototype
Android Application intended to help label image data. This is currently only useful for classification problems. The API designed to work with this app can be found 
[here](https://github.com/Jake-Jay/datalabellerServer).


## Instructions to clone project:

__Option 1__

1. Open android studio and select 'Check out project from Version Control'
2. Select GitHub from the dropdown list.
3. Enter your GitHub credentials. Then click Login
4. Fill out the Clone Repository form and click Clone
5. Open the project
6. Once the application is open click the button in the top right corner that says "sync project with gradle files"

__Option 2__ (if Android Studio is already open)

1. Select VCS > Git > Clone
2. Android Studio will promt for the new application to be opened
3. Once the application is open click the button in the top right corner that says "sync project with gradle files"


## Running the Project

The application should run on all modern Android API versions. Simply click run and choose your device.

### Connect to the server
The application is currently set up to work on a local network. It accesses a server at http://10.20.20.101:5000/

To change this the __BASE_URL__ needs to be changed in the following files:

- NetworkUtils.java
- AddLabel.java
- RegisterUser.java
