# Beer

This is an Android app written in Kotlin, which displays a list of beers. User can filter beers by using tags and by typing a keyword in search view. The orientation of the Activity is not blocked.

<br>

<p align="center">
  <img src="readme/screenrecord.gif" width="300">
</p>

#### This app has following packages:
1. **activities**: It contains activities interacting with the user.
2. **adapters**: It contains adapters which provide access to the data items.
3. **extensions**: Utility classes.
4. **models**: It contains model objects.
5. **services**: It contains services and APIs.
6. **viewModels**: It contains all the viewModels.

#### This is a MVVM kotlin project with following features:
1. **Retrofit** to consume REST API. In particular I used Punk API to get all beer information such as name, tagline, imageUrl etc.
2. **ViewModel** to save and manage UI-related data.
3. **LiveData** to create observable objects that respects lifecycle of other app components.
4. **StateFlow** to be exposed from ViewModel so that the View can listen for UI state updates.
5. **Coroutines** to convert async callbacks for long-running tasks into sequential code.
6. **viewBinding** to generate a binding class for each XML layout file.

<br>

<p align="center">
  <img src="screenshot1.png" width ="150" style="margin: 2px;"/>
  <img src="screenshot2.png" width ="150" style="margin: 2px;"/>
  <img src="screenshot3.png" width ="150" style="margin: 2px;"/>
  <img src="screenshot4.png" width ="150" style="margin: 2px;"/>
  <img src="screenshot5.png" width ="150" style="margin: 2px;"/>
</p>


N.B.: The filtering condition is whether any of name, description or tagline of each item contains the selected filter tag name.