# K-Means Case Study

This project is a starting point for an exercise to speedup an Java application by applying parallel concepts.

Starting from a simple naive implementation it is step by step shown how to apply several patterns to increase reactivity and response time.

This project is part of a lesson hold on the university of applied sciences Kaiserslautern.

Usage condition:
* OpenJDK 11 is required
* Dependencies to JavaFX and other libraries are managed by Maven
* Tested on Win 10

Main class: `app.SimpleKMeans`


---

Performance bottlenecks and problems to solve in the exercise: 

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
5. Build-in timeout constraints for the asynchron tasks

---
