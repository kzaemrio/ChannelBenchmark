# ChannelBenchmark

```
channel actor:

produceInc(new single thread), produceDec(new single thread)
-> channel 
-> actor(new single thread)
```

```
executor:

produceInc(new single thread), produceDec(new single thread)
-> blocking queue 
-> consumer(new single thread)
```

```
-Xmx4g -Xms4g -Xmn2g

@Fork(5)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)

// inc and dec repeat times
n = 99999

// size for actorBUFFERED and executorArrayBlockingQueue
bufferSize = n / 3
```

| Benchmark                              | Mode | Cnt | Score | Error   | Units |
|:---------------------------------------|------|-----|-------|---------|------:|
| actorRENDEZVOUS                        | avgt | 25  | 0.209 | ± 0.048 |  s/op |
| actorBUFFERED(bufferSize)              | avgt | 25  | 0.031 | ± 0.001 |  s/op |
| actorUNLIMITED                         | avgt | 25  | 0.035 | ± 0.001 |  s/op |
| executorSynchronousQueue               | avgt | 25  | 0.053 | ± 0.001 |  s/op |
| executorArrayBlockingQueue(bufferSize) | avgt | 25  | 0.016 | ± 0.001 |  s/op |
| executorLinkedBlockingQueue            | avgt | 25  | 0.029 | ± 0.001 |  s/op |
