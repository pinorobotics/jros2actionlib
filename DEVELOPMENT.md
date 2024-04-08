# Build

Building module locally and making changes to it (this is optional and not intended for users).

## With Eclipse

- Build Eclipse projects:

``` bash
gradle eclipse
```

- Import them into Eclipse

# Release steps

- Update [Android dependencies](android/build.gradle) and run `gradle clean build -b android/build.gradle`
- Close version in gradle.properties
- Run `gradle clean build javadoc`
- Publish
- Open next SNAPSHOT version
- Update CHANGELOG.md with new release (for changelog generation use `git log --format=%s`)
- Commit changes
- Push
- Upload documentation to website
