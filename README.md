# CS1632_Deliverable6
## Deliverable 6 - ChatApp: A Multiple Client and Server Chat Application Achieved Through TDD

#####Note: This project was created using IntelliJ

##Instructions to run in Eclipse:

###1) Clone using git
    git clone https://github.com/rickykoter/CS1632_Deliverable6.git

###2) Create a project

Create a new eclipse project: File -> New -> Java Project and give it any name
(i.e. ChatApp or Deliverable6).

###3) Import the contents of the cloned project
 1) File -> Import...
 
 2) Select File System then Next
 
 3) 'From Directory' should be your cloned project's directory
 
 4) Select all files and folders
 
 5) 'Into Folder' should be the directory of the newly created project.
 
 6) Select Finish

###4) Add the jUnit library

 1) Right click the project in the Package Explorer and selecting Build Path -> Add Libraries.
 
 2) Then select JUnit and click Next >.
 
 3) Select JUnit4 from the drop down.
 
 4) Select Finish.
 
###5) Add the Mockito library

1) Right click the project in the Package Explorer and selecting Build Path -> Add External Archives.

2) Then choose the mockito jar included in this project:
  <your path to the project directory>\lib\mockito-all-1.10.19.jar

3) Select Ok.

###6) Run the program and tests
The main ChatApp class is in the "src" directory, and the tests are in "test" directory.
If not already set as source folders, add both directories (test and src) to the Build Path by right clicking each directory in Eclipse and selecting Build Path -> Use as Source Folder.

