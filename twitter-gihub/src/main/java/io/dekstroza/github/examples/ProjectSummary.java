package io.dekstroza.github.examples;

import io.dekstroza.github.examples.twitter.Tweet;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents project summary, keeping information about project name, description and list of tweets.
 * Project name and description are immutable, and can be only set via constructor.
 */
public class ProjectSummary {
    private final String projectName;
    private final String projectDescription;
    private List<Tweet> tweets = new ArrayList<>();

    /**
     * Constructor used to create instacnce of Project Summary
     *
     * @param projectName
     *            Name of the project
     * @param projectDescription
     *            Description of this project
     */
    public ProjectSummary(final String projectName, final String projectDescription) {
        this.projectName = projectName;
        this.projectDescription = projectDescription;
    }

    /**
     * Will return name of this project.
     *
     * @return String representing project name, as reported by github.
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * Will return description of this project.
     *
     * @return String representing description of this project, as reported by github.
     */
    public String getProjectDescription() {
        return projectDescription;
    }

    /**
     * Get all tweets for this project. Empty by default, and should be set via setter method.
     *
     * @return List of tweets about this project.
     */
    public List<Tweet> getTweets() {
        return tweets;
    }

    /**
     * Setter method for tweets about this project.
     *
     * @param tweets
     *            List of tweets about this project.
     */
    public void setTweets(final List<Tweet> tweets) {
        this.tweets = tweets;
    }

}
