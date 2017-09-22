package io.dekstroza.github.examples;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.dekstroza.github.examples.github.GithubApi;
import org.slf4j.Logger;

import static io.dekstroza.github.examples.github.GithubApi.searchGithub;
import static io.dekstroza.github.examples.twitter.TwitterApi.getTwitterApiInstance;
import static java.lang.String.format;
import static org.slf4j.LoggerFactory.getLogger;

public class GithubTwitterApplication {

    private static final Logger log = getLogger(GithubTwitterApplication.class);

    public static void main(String[] args) {

        System.out.println("... Ten reactive github projects and tweets about them ...");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        long executionDuration = System.nanoTime();
        searchGithub().limit(10).parallel().forEach(projectSummary -> {
            projectSummary.setTweets(getTwitterApiInstance().searchTwitter(projectSummary.getProjectName()));
            System.out.println(gson.toJson(projectSummary));
        });
        executionDuration = System.nanoTime() - executionDuration;
        System.out.println(format("Github search duration: %s ms.", GithubApi.getInvocationDuration()));
        System.out.println(format("Average twitter search duration: %s ms.", getTwitterApiInstance().getAverageDurationTimeMs()));
        System.out.println("---------------------------------------------");
        System.out.println(format("Total duration: %s ms.", executionDuration / 1000000));
    }

}
