# LFU
cons （缺点）:
1. 初次放进去的元素，frequency 都很小。
2. 开始被很平凡使用，而后不用的元素，需要一段时间才会被移除。
https://en.wikipedia.org/wiki/Least_frequently_used

实现(java)：
https://github.com/ayun01/system-design/blob/master/LFU-cache.java
1. HashMap 作为cache保存value。（对于HashMap，如果put一个已经存在的key，等于update其value）
2. LinkedHashset<int,LinkedHashSet<CacheNode>> 作为frequencyList保存Frequency。（LinkedHashset就是一个保留了插入次序的set）
3. 用一个minFrequency,maxFrequency来保留上下bound。初始值为0和2 × capacity. minFrequency 在需要eviction的时候，从minFrequency开始。
4. PUT()
- 如果大于capacity，do eviction。EVICTION - 把cache和frequencyList里minFrequency里最老的那个元素【0】去掉。根据需要调节minFrequency。
- 如果key不存在，添加此元素到cache 和 frequency为0的frequencyList中去。minFrequency = 0. 
- 如果存在就，就update其value。最后increase frequency。
5. Increase frequency 
- frequency# + 1
- 把当前元素从frequency#的list里移到frequency# + 1里(frequencyList)。
- 如果frequencyList中minFrequency为空，minFrequency += 1
6. GET()
- 返回key的值。
- 最后increase frequency

# LRU

实现(java)：
https://github.com/ayun01/system-design/blob/master/LRU-cache.java
1. 双向链表来表示使用的frequency，越靠head的是越久之前使用的，越靠tail的是最近才使用过的。
2. HashMap 作为cache保存value。其值指向每个node.

https://en.wikipedia.org/wiki/Cache_replacement_policies#LRU
