# Code Scanner
Android app for scanning barcodes built with Jetpack Compose, Camera2, ML Kit, Room, Paging3, DataStore

### Scanning modes
- Single code - scanning stops after detecting one code
- Multiple codes - scanned codes are added to the list
- Selected codes - detected codes are marked on the screen and added to the list when you click on them
- Highlight code - allows you to enter a code that will be marked when detected on the screen

### Database
Scans are stored in a database using Room. The list of scans is paginated.

### Settings
The application has a settings panel where you can:
- set the types of codes detected,
- set the app language,
- enable/disable the code detection sound for the Highlight code mode.

Settings are saved in the DataStore.