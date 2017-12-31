# LFU
cons （缺点）:
1. 初次放进去的元素，frequency 都很小。
2. 开始被很平凡使用，而后不用的元素，需要一段时间才会被移除。
https://en.wikipedia.org/wiki/Least_frequently_used

实现(java)：
1. HashMap 作为cache保存value。（对于HashMap，如果put一个已经存在的key，等于update其value）
2. LinkedHashset 作为frequencyList保存Frequency。（LinkedHashset就是一个保留了插入次序的set）
3. 用一个minFrequency,maxFrequency来保留上下bound。初始值为0和2*capacity
