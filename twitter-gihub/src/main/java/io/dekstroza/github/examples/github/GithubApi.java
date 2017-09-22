package io.dekstroza.github.examples.github;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import io.dekstroza.github.examples.ProjectSummary;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.lang.String.format;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Representation of github api
 */
public class GithubApi {

    private static final String githubURL = "https://api.github.com/search/repositories?q=reactive";
    private static final Logger log = getLogger(GithubApi.class);
    private static long invocationDuration = 0L;

    /**
     * Search github for keyword reactive. This method will return stream of incomplete ProjectSummary instances,
     * which will have project name and descripton set, but not the tweets.
     *
     * @return Stream of incomplete project summaries, they will contain only project name and project description.
     */
    public static Stream<ProjectSummary> searchGithub() {
        try {
            final OkHttpClient client = new OkHttpClient();
            final Request request = new Request.Builder().url(githubURL).build();
            invocationDuration = System.nanoTime();
            final ReadContext ctx = JsonPath.parse(client.newCall(request).execute().body().byteStream());
            invocationDuration = System.nanoTime() - invocationDuration;
            final List<String> projectNames = ctx.read("$.items[*].name", List.class);
            final List<String> projectDescriptions = ctx.read("$.items[*].description", List.class);
            final List<ProjectSummary> projectSummaryList = new ArrayList<>();
            for (int i = 0; i < projectNames.size(); i++) {
                projectSummaryList.add(new ProjectSummary(projectNames.get(i), projectDescriptions.get(i)));
            }
            return projectSummaryList.stream();
        } catch (IOException ioe) {
            throw new IllegalStateException(format("Unable to get projects from github.Error:%s", ioe.getMessage()));
        }
    }

    /**
     * Calculate duration of search and response reading for github search.
     *
     * @return Duration of github search in milliseconds.
     */
    public static double getInvocationDuration() {
        return invocationDuration / 1000000;
    }
}
