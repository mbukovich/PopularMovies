# PopularMovies
An app that allows users to view details of popular movies.

This app was created as a project for Udacity's Android Developer Nanodegree.
The app uses a RecyclerView to dynamically display data obtained from the 
TMDB API. The app utilizes Picasso to display images (url information gained
from the TMDB API). A click listener is used to bring the user to a new activity
showing details about the movie clicked on. In addition, a spinner control
allows the user to choose movies sorted by popularity or by rating. Each time
a sort option is chosen, data is obtained again from the API.

This particular example of the project only needs to contact the API once per
sort since all the data is stored in a HashMap.


Important: anyone building this project will need to use his/her own key for
the TMDB API. This can be obtained by creating an account at -
https://www.themoviedb.org/account/signup
And requesting an API key once a member.

After obtaining an API key, the developer will need to paste this key in the
/res/values/strings.xml file. The string is at the top of the file and easy to
find. Once the key is pasted, the project can be built and run. Please remove
your API key before committing or uploading source code.
