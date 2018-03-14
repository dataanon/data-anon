# Data::Anonymization

Data Anonymization tool helps build anonymized production data dumps, 
which you can use for performance testing, security testing, debugging and development.
Tool is implemented in Kotlin, and works with Java 8 & Kotlin.

[![Build Status](https://travis-ci.org/dataanon/data-anon.svg?branch=master)](https://travis-ci.org/dataanon/data-anon)

## Getting started

### Kotlin

```kotlin
fun main(args: Array<String>) {
    // define your database connection settings 
    val source = DbConfig("jdbc:h2:tcp://localhost/~/movies_source", "sa", "")
    val dest = DbConfig("jdbc:h2:tcp://localhost/~/movies_dest", "sa", "")

    // choose Whitelist or Blacklist strategy for anonymization
    Whitelist(source, dest)
            .table("MOVIES") {  // start with table                                
                where("GENRE = 'Drama'")    // allows to select only desired rows (optional)
                limit(1_00_000)             // useful for testing (optional)

                // pass through fields, leave it as is (do not anonymize)
                whitelist("MOVIE_ID")

                // field by field decide the anonymization strategy
                anonymize("GENRE").using(PickFromDatabase<String>(source,"SELECT DISTINCT GENRE FROM MOVIES"))
                anonymize("RELEASE_DATE").using(DateRandomDelta(10))

                // write your own in-line strategy
                anonymize("TITLE").using(object: AnonymizationStrategy<String>{
                    override fun anonymize(field: Field<String>, record: Record): String = "MY MOVIE ${record.rowNum}"
                })
            }

            // continue with multiple tables
            .table("RATINGS") {
                whitelist("MOVIE_ID","USER_ID","CREATED_AT")
                anonymize("RATING").using(FixedDouble(4.3))
            }
            .execute()
}
```

### Java

```java
public class Anonymizer {

    public static void main(String[] args) {

        // define your database connection settings
        DbConfig source = new DbConfig("jdbc:h2:tcp://localhost/~/movies_source", "sa", "");
        DbConfig dest = new DbConfig("jdbc:h2:tcp://localhost/~/movies_dest", "sa", "");

        // choose Whitelist or Blacklist strategy for anonymization
        new Whitelist(source, dest)

            // start with table
            .table("MOVIES", table -> {
                table.where("GENRE = 'Drama'");     // allows to select only desired rows (optional)
                table.limit(1_00);                  // useful for testing (optional)

                // pass through fields, leave it as is (do not anonymize)
                table.whitelist("MOVIE_ID");

                // field by field decide the anonymization strategy
                table.anonymize("GENRE").using(new PickFromDatabase<String>(source,"SELECT DISTINCT GENRE FROM MOVIES"));
                table.anonymize("RELEASE_DATE").using(new DateRandomDelta(10));

                // write your own in-line strategy
                table.anonymize("TITLE").using((AnonymizationStrategy<String>) (field, record) -> "MY MOVIE " + record.getRowNum());

                // return just to cover up for Kotlin, copy as is
                return Unit.INSTANCE;
            })

            // continue with multiple tables
            .table("RATINGS", table -> {
                table.whitelist("MOVIE_ID", "USER_ID", "CREATED_AT");
                table.anonymize("RATING").using(new FixedDouble(4.3));
                return Unit.INSTANCE;
            })
            .execute(true);
    }
}
```


## Running

```shell
$ mvn compile exec:java
```

Or

```shell
$ mvn package
$ java -jar target/data-anon.jar
```

## Start with samples...

* [Kotlin](https://github.com/dataanon/dataanon-kotlin-sample)
* [Java](https://github.com/dataanon/dataanon-java-sample) 

----------------------
## Notes/tips on usage of tool

1. In Whitelist approach provide source database connection user with READONLY access.
1. All messages (incl. errors) are logged in `dataaonn.log` file. Using `logging.properties` control log messages.
1. Use `where` and `limit` to limit the number of rows during anonymization. Very useful for testing purpose.
1. Extend `DbConfig` and implement `connection` method for special handling while creating database connection.
1. Write your [own anonymization strategy](#write-your-own-anonymization-strategy) for specific cases.
1. `null` values are kept `null` after anonymization.
1. Use `PickFromDatabase` strategy for picking values for existing values and `enum` type values.

----------------------

## Roadmap

1. Support default strategy based on data type. As of now you need to specify anonymization strategy for each field.
1. MongoDB database.

## Share feedback

Please use Github [issues](https://github.com/dataanon/data-anon/issues) to share feedback, feature suggestions and report issues.

## Changelog

#### 0.9.3 (Mar 214, 2018)

1. Error handling using logs. All messages logged in `dataaonn.log` file. Using `logging.properties` control log messages.
1. Added date and timestamp related anonymization strategies - DateRandomDelta & DateTimeRandomDelta
1. PickFromDatabase strategy added to support enum types fields picked up from database column. Useful for names type fields as well.

#### 0.9.1 (Feb 26, 2018)

1. First initial release with RDBMS support
1. First class support for table parallelization providing better performance
1. Easy to use DSL built using Kotlin

----------------------

## What is data anonymization?

For almost all projects there is a need for production data dump in order to run performance tests, rehearse production releases and debug production issues.
However, getting production data and using it is not feasible due to multiple reasons, primary being privacy concerns for user data. And thus the need for data anonymization.
This tool helps you to get anonymized production data dump using either Blacklist or Whitelist strategies.

Read more about [data anonymization here](http://sunitspace.blogspot.in/2012/09/data-anonymization.html)

## Anonymization Strategies

### Blacklist
Blacklist approach essentially leaves all fields unchanged with the exception of those specified by the user, which are scrambled/anonymized.
For `Blacklist` create a copy of prod database and chooses the fields to be anonymized like e.g. username, password, email, name, geo location etc. based on user specification. Most of the fields have different rules e.g. password should be set to same value for all users, email needs to be valid.

The problem with this approach is that when new fields are added they will not be anonymized by default. Human error in omitting users personal data could be damaging.

```kotlin
Blacklist(database)
    .table("RATINGS") {
        anonymize("RATING").using(FixedDouble(4.3))
    }
.execute()
```

### Whitelist
Whitelist approach, by default scrambles/anonymizes all fields except a list of fields which are allowed to copied as is.
By default all data needs to be anonymized. So from production database data is sanitized record by record and inserted as anonymized data into destination database. Source database needs to be readonly.
All fields would be anonymized using default anonymization strategy which is based on the datatype, unless a special anonymization strategy is specified. For instance special strategies could be used for emails, passwords, usernames etc.
A whitelisted field implies that it's okay to copy the data as is and anonymization isn't required.
This way any new field will be anonymized by default and if we need them as is, add it to the whitelist explicitly. This prevents any human error and protects sensitive information.

```kotlin
Whitelist(source, dest)
    .table("RATINGS") {
        whitelist("MOVIE_ID","USER_ID","CREATED_AT")
        anonymize("RATING").using(FixedDouble(4.3))
    }
.execute()
```

Read more about [blacklist and whitelist here](http://sunitspace.blogspot.in/2012/09/data-anonymization-blacklist-whitelist.html)


----------------------
## Anonymization Strategies

| DataType              | Stratergy                     | Description                                               |
| :---                  | :---                          | :---                                                      |
| Boolean               | RandomBooleanTrueFalse        | random selection of boolean true and false value          |
| Boolean               | RandomBooleanOneZero          | random selection of 1 and 0 value representing boolean    |
| Boolean               | RandomBooleanYN               | random selection of Y and N value representing boolean    |
| String (Email)        | RandomEmail                   | generates emailId using one of random picked values defined in specified file (default file is first_names.dat) appended with row number with given host and tld |
| String (First Name)   | RandomFirstName               | generates first name using one of random picked values from specified file. default file is (first_names.dat) |
| String (Last Name)    | RandomLastName                | generates last name using one of random picked values from specified file. default file is (last_names.dat) |
| Integer               | FixedInt                      | replace all records with the same specified fixed integer value (default to 100) |
| Integer               | RandomInt                     | generate random integer value between specified range (default range from 0 to 100) |
| Integer               | RandomIntDelta                | generate new integer value within random delta value (default is 10) on existing value |
| Float                 | FixedFloat                    | replace all records with the same specified fixed float value (default to 100.0f) |
| Float                 | RandomFloat                   | generate random float value between specified range (default range from 0.0f to 100.0f) |
| Float                 | RandomFloatDelta              | generate new float value within random delta value (default is 10.0f) on existing value |
| Double                | FixedDouble                   | replace all records with the same specified fixed double value (default to 100.0) |
| Double                | RandomDouble                  | generate random double value between specified range (default range from 0.0 to 100.0) |
| Double                | RandomDoubleDelta             | generate new double value within random delta value (default is 10.0) on existing value |
| String                | FixedString                   | replace all records with the same specified fixed string value |
| String                | LoremIpsum                    | replace with same length (size) Lorem Ipsum string |
| String                | PickStringFromFile            | replace with randomly picked string (line) from file |
| String                | RandomAlphabetic              | replace with random alphabets char set only creating string |
| String                | RandomAlphaNumeric            | replace with random alphabets + numbers (alphanumeric char set) creating string |
| String                | RandomFormattedString         | replace with string build with exactly same format (number replacing number, lowercase replacing lowercase alphabets & uppercase replacing uppercase alphabets |
| String                | RandomString                  | replace with random generation of string of any char set |
| String                | StringTemplate                | replace with string generated using template specified |
| Date                  | DateRandomDelta               | date field is changed randomly within given range of days |
| Timestamp             | DateTimeRandomDelta           | timestamp (datetime) field is changed randomly within given duration |
| Any                   | PickFromList                  | replaces value with specified type and randomly picked from list of specified values |
| Any                   | PickFromDatabase              | replaces value with specified type and randomly picked from list of specified values fetched from database |

## Write your own Anonymization strategy

Implement `AnonymizationStrategy` interface `override` method to write your own strategy.

```kotlin
class RandomString: AnonymizationStrategy<String> {
    override fun anonymize(field: Field<String>, record: Record): String = "Record Number ${record.rowNum}"
}
```

`Field` class represents data related to the field getting processed for anonymization

```kotlin
class Field<T: Any>(val name: String, val oldValue: T, var newValue: T = oldValue)
```

`Record` class represents the current record getting processed with row number and all the fields of the record.
Other fields data is useful in case if there is any dependent field value which needs to be derived or calculated.

```kotlin
class Record(private val fields: List<Field<Any>>, val rowNum: Int) {
    fun find(name: String): Field<Any> = fields.first {name.equals(it.name, true)}
}
```

It is very easy to write inline strategy as well. See examples

* [Kotlin](https://github.com/dataanon/dataanon-kotlin-sample/blob/master/src/main/kotlin/com/github/dataanon/Anonymizer.kt#L20)
* [Java](https://github.com/dataanon/dataanon-java-sample/blob/master/src/main/java/com/github/dataanon/Anonymizer.java#L20)


## Want to contribute?

1. Fork it
2. Create your feature branch (`git checkout -b my-new-feature`)
3. Commit your changes (`git commit -am 'Add some feature'`)
4. Push to the branch (`git push origin my-new-feature`)
5. Create new Pull Request

## Credits

- [ThoughtWorks Inc](http://www.thoughtworks.com), for allowing us to build this tool and make it open source.