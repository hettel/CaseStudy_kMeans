# K-Means Case Study

This project is a starting point for an exercise to speedup the Java application by applying parallel concepts.

Starting from a simple naive implementation (the code provided in this repository) 
students should apply several patterns to increase reactivity and response time of the application.

This project is part of the lesson *Java Concurrency* hold on the university of applied sciences Kaiserslautern.

Some remarks
* The project was build with OpenJDK 11 
* The application uses [oshi](https://github.com/oshi/oshi) (Native Operating System and Hardware Information)
* Dependencies to JavaFX and the oshi libraries are managed by Maven

Main class: `app.SimpleKMeans`

Build command:

`mvn clean package`

The command create a lib and module directory that contains the dependencies and module runnable.

Start the application:

`java -p modules;lib -m kMeansCaseStudy/app.SimpleKMeans`

---

### The Show Case Application

The application offers a simple user interface. You can load a directory with jpg- and png-files. The content of the directory are shown as small preview images below on the UI.

A selected image is displayed on the left and can be color reduced by using the k-means algorithm. The value for k can be selected. After processing the found colors and the color reduced image are shown. The colored reduced image can be saved as an png-file. Note that the result of the k-means algorithm is not deterministic.

<img src="images/ui.jpg" alt="drawing" width="600"/>

---

### Exercise

Performance bottlenecks and problems to be solved: 

1. Start of the application (the method `initialize` in the class `app.ui.UIController`)
2. The creation of the color list in `kmeans.imagedata.util.KMeansImage`
3. Loading and creation of the preview images in `util.FileIOHelper`
4. Calculation of the color reduced image in the class `kmeans.KMeans`
5. During the calculation of the color reduced image the application is not responsive 

Refactoring tasks:

1. Decouple the hardware initialization from the start-up (task parallelism)
2. Decouple the k-means calculation from the JavaFX thread (task parallelism)
3. Improve the loading of the pre images (data parallelism)
4. Changing the k-means algorithm so that parallel Java-Streams are used. Introduce appropriate user defined collectors (data parallelism)
5. Build-in timeout constraints for the asynchronous tasks

---
