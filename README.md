# Beer

This is an Android app written in Kotlin, which displays a list of beers. The user can filter beers by using tags and by searching. The orientation of the Activity is not blocked.

<br>

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
4. **Coroutines** to convert async callbacks for long-running tasks into sequential code.
5. **View Binding** to generate a binding class for each XML layout file.

<br>

<div style="display:flex">
     <div style="flex:1;padding-right:10px;">
          <img src="readme/screenrecord.gif" width="200"/>
     </div>
     <div style="flex:1;padding-left:10px;">
          <img src="readme/screenshot_dark.png" width="200"/>
     </div>
     <div style="flex:1;padding-left:10px;">
          <img src="readme/screenshot_light.png" width="200"/>
     </div>
</div>