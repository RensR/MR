package helpers;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;
import com.google.gdata.client.youtube.YouTubeQuery;
import com.google.gdata.client.youtube.YouTubeService;
import com.google.gdata.data.youtube.VideoEntry;
import com.google.gdata.data.youtube.VideoFeed;
import com.google.gdata.data.youtube.YouTubeMediaGroup;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.IOException;
import play.Logger;

public class Youtube {
    public void main(String message){ 
    }

    public String getVideoIdFromQuery(String message){
        String youtubeVideoId = "";
        try{
            youtubeVideoId = "Fail";
            YouTubeQuery query = new YouTubeQuery(new URL("http://gdata.youtube.com/feeds/api/videos"));
            // order results by the number of views (most viewed first)
            query.setOrderBy(YouTubeQuery.OrderBy.VIEW_COUNT);
            
            // search for puppies and include restricted content in the search results
            query.setFullTextQuery(message);
            query.setSafeSearch(YouTubeQuery.SafeSearch.NONE);

            YouTubeService service = new YouTubeService("magicalmusic");

            Logger.debug("Starting service.query");
            VideoFeed videoFeed = service.query(query, VideoFeed.class);
            Logger.debug("Ending service.query");

            VideoEntry bestHit = videoFeed.getEntries().get(0);
            YouTubeMediaGroup mediaGroup = bestHit.getMediaGroup();
            youtubeVideoId = mediaGroup.getVideoId();
    }
    catch(java.io.IOException ex)
    {
        Logger.debug("The error is " + youtubeVideoId);
        throw ex;
    }
    finally{
        Logger.debug("The result is " + youtubeVideoId);
        return youtubeVideoId;
        }
    }
}