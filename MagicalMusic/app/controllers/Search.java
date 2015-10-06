package helpers;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class Search {

    private static final long NUMBER_OF_VIDEOS_RETURNED = 3;
    private static YouTube youtube;

    public String getVideoIDFromQuery(String query)
    {
        try {
            // This object is used to make YouTube Data API requests. The last
            // argument is required, but since we don't need anything
            // initialized when the HttpRequest is initialized, we override
            // the interface and provide a no-op function.
            youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
                public void initialize(HttpRequest request) throws IOException {
                }
            }).setApplicationName("youtube-cmdline-search-sample").build();

            // Define the API request for retrieving search results.
            YouTube.Search.List search = youtube.search().list("id,snippet");

            // Set your developer key from the Google Developers Console for
            // non-authenticated requests. See:
            // https://console.developers.google.com/
            String apiKey = "AIzaSyA2i-v--qPA0SiFt_8-e-ri47A9gVPuwQE";

            search.setKey(apiKey);
            search.setQ(query);

            // Restrict the search results to only include videos. See:
            // https://developers.google.com/youtube/v3/docs/search/list#type
            search.setType("video");
            search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);

            // Call the API and print results.
            SearchListResponse searchResponse = search.execute();
            List<SearchResult> searchResultList = searchResponse.getItems();
            if (searchResultList != null) {
                String id = getID(searchResultList.iterator());
                return id;
            }
        } catch (GoogleJsonResponseException e) {
            return "There was a service error: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage();
        } catch (IOException e) {
            return "There was an IO error: " + e.getCause() + " : " + e.getMessage();
        } catch (Throwable t) {
            return t.toString();
        }
        return "Fail";
    }

    /*
     * Get the video ID
     * @param iteratorSearchResults Iterator of SearchResults
     */
    private static String getID(Iterator<SearchResult> iteratorSearchResults) {
        if (!iteratorSearchResults.hasNext()) {
          //No results were found for the query
            return "No results.";
        }

        while (iteratorSearchResults.hasNext()) {
            SearchResult singleVideo = iteratorSearchResults.next();
            ResourceId rId = singleVideo.getId();

            // Confirm that the result represents a video. Otherwise, the
            // item will not contain a video ID.
            if (rId.getKind().equals("youtube#video")) {
                //return the first video.
                return rId.getVideoId();
            }
        }
        return "Fail";
    }
}