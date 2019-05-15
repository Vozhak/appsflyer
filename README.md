LAST.FM Analytics
=================

Service, which produces a list of top 10 songs played in the top 50 longest user sessions by tracks count.



Requirements
------------

* sbt
* Spark Distribution (> 2.*)

Setup
-----

### Building a Fat Jar

```bash
sbt assembly
```

### Submitting app to run locally

```bash
./$SPARK_HOME/bin/spark-submit 
    --master local[*] 
    --class me.glowacki.Main  
    --driver-memory [MEMORY_SIZE (e.g. 2G, 1000M)] 
    --conf spark.driver.maxResultSize=2G 
    [APPLICATION.JAR] 
    [PATH_TO_DATASET] 
    [LOCAL_OUTPUT_PATH]
```

Deploy/Run instructions: [Submitting Applications](https://spark.apache.org/docs/latest/submitting-applications)

Dataset can be found here: [Last.fm dataset](http://mtg.upf.edu/static/datasets/last.fm/lastfm-dataset-1K.tar.gz)

Spark distribution can be downloaded from here: [Spark Downloads](https://spark.apache.org/downloads)
