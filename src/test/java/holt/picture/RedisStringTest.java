package holt.picture;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class to learn how to use Redis
 * @author Weiyang Wu
 * @date 2025/4/26 22:16
 */
@SpringBootTest
public class RedisStringTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void testRedisStringOperations() {
        // Get operation object
        ValueOperations<String, String> valueOps = stringRedisTemplate.opsForValue();

        // Key and Value
        String key = "testKey";
        String value = "testValue";

        // 1. Test set
        valueOps.set(key, value);
        String storedValue = valueOps.get(key);
        assertEquals(value, storedValue, "Values are not equal");

        // 2. Test update
        String updatedValue = "updatedValue";
        valueOps.set(key, updatedValue);
        storedValue = valueOps.get(key);
        assertEquals(updatedValue, storedValue, "Values are not equal after update");

        // 3. Test get
        storedValue = valueOps.get(key);
        assertNotNull(storedValue, "查询的值为空");
        assertEquals(updatedValue, storedValue, "Values are not equal when getting");

        // 4. Test delete
        stringRedisTemplate.delete(key);
        storedValue = valueOps.get(key);
        assertNull(storedValue, "Value is not null after deletion");
    }
}
