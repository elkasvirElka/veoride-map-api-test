## Interview coding task
## Summary
# In this app you can do following things:
- Open the map
- Choose destination, app will show your location (after you will give permission)
- Start you trip and see traveling path
- Stop you trip at any time
- When you will get you destination app will stop your trip automatically
- After your trip is finished, you can see summary of the trip that includes:
	- a. Your traveled path (shows on the map)
	- b. The elapsed trip time
	- c. The total distance traveled

## Steps I took to develop this app
- The app was created by following:
  - a. SOLID
  - b. clean archetecture pattern
  - c. Repository pattern
  - d. Dependence injection(using Dagger2 lib)
  - e. MVVM and LiveData
 - The app can save state after rotation and swiching to another app(SavedStateHandle)
 - The app is using service to get current location update
 - The app is BroadcastReceiver to make changes on the map during your trip
   - if you will go wrong way the app will change your route
   
 ## The video of app flow
 https://youtu.be/Q8PEguO1h00
