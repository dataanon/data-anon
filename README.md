# Data::Anonymization
Afraid of using production data due to privacy issues? 
Data Anonymization is a tool that helps you build anonymized production data dumps which you can use for 
performance testing, security testing, debugging and development.
Tool is implemented in Kotlin. Works with Java & Kotlin.

[![Build Status](https://travis-ci.org/dataanon/data-anon.svg?branch=master)](https://travis-ci.org/dataanon/data-anon)

## Getting started

```kotlin
fun main(args: Array<String>) {
    // define your database connection settings 
    val source = DbConfig("jdbc:h2:tcp://localhost/~/movies_source", "sa", "")
    val dest = DbConfig("jdbc:h2:tcp://localhost/~/movies_dest", "sa", "")

    Whitelist(source,dest) // choose Whitelist or Blacklist strategy for anonymization
            .table("MOVIES") {  // start with table                                
                where("GENRE = 'Drama'")    // allows to select only desired rows
                limit(1_00_000)             // useful for testing
                // for whitelist strategy, list fields that needs to just pass through as is
                whitelist("MOVIE_ID","RELEASE_DATE")
                // now field by field decide the anonymization strategy 
                anonymize("GENRE").using(FixedString("Action"))
                anonymize("TITLE").using(object: AnonymizationStrategy<String>{
                    // write your own in-line strategy
                    override fun anonymize(field: Field<String>, record: Record): String = "MY MOVIE ${record.rowNum}"
                })
            }
            .table("RATINGS") {  // continue with more tables
                whitelist("MOVIE_ID","USER_ID","CREATED_AT")
                anonymize("RATING").using(FixedDouble(4.3))
            }
            .execute()
}
```

Sample Maven based projects are available here...
[Kotlin](https://github.com/dataanon/dataanon-kotlin-sample)
[Java](https://github.com/dataanon/dataanon-java-sample) 

### Running

    $ mvn compile exec:java
    
    Or 
    
    $ mvn package
    $ java -jar target/data-anon.jar 
         

### Liked it? please share

[<img src="https://si0.twimg.com/a/1346446870/images/resources/twitter-bird-light-bgs.png" height="35" width="35">](https://twitter.com/share?text=A+simple+ruby+DSL+based+data+anonymization&url=http:%2F%2Fsunitparekh.github.com%2Fdata-anonymization&via=dataanon&hashtags=dataanon)

