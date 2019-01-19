# K-Means-CaseStudy

This project is a starting point for an exercise to speedup an Java application by applying parallel concepts.

Starting from a simple naive implementation it is step by step shown how to apply several patterns to increase reactivity and response time.

This project is part of a lesson hold on the university of applied sciences Kaiserslautern.

Usage condition:
* OpenJDK 11 is required
* Dependencies to JavaFX and other libraries are managed by Maven

Main class: `app.SimpleKMeans`


---

Exercise steps: 

1. Decouple the k-means calculation from the JavaFX thread
2. Changing the k-means algorithm so that Java-Streams are used
3. Improve the startup time of the application
4. Improve the loading of the pre images
5. Improve the k-means calculation time by using parallel Streams. Introduce appropriate user defined collectors
6. Decouple the upload request from the JavaFX thread
7. Build-in timeout constraints for the upload process

All changes can be made in the classes `kmeans.KMeans.java` and `kmaens.imagedata.util.Tools.java`,`util.FileIOHelper.java` and `app.ui.UIController.java`

---
