# Github Twitter Mashup
## Build and run instructions

1. Obtain consumer key and secret key for twitter
2. Create file called twitter-creds.properties in same directory with pom file
3. Add into the twitter-creds.properties:
    ```
    consumer.key=YOUR_CONSUMER_KEY_GOES_HERE
    consumer.secret=YOUR_SECRET_KEY_GOES_HERE
    ```
    
4. Build with: 
   ```maven clean install```
5. Run with: 
   ```java -jar target/twitter-github-1.0.1-SNAPSHOT-jar-with-dependencies.jar``` 
