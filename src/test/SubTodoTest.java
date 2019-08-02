import model.SubTodo;
import model.SuperTodo;
import model.Todo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class SubTodoTest {
    SubTodo todo;
    SuperTodo st;

    @BeforeEach
    public void init() {
        todo = new SubTodo("CPSC 210 HWK", "Wednesday");
        st = new SuperTodo("testSuper", "test");
    }

    @Test
    public void testSetAllThreeParamConstrutor() {
        todo = new SubTodo("test", "test", true);
        todo.printTodo(1);
        todo.addSuper(st);
        assertEquals("test", todo.getName());
        assertEquals("test", todo.getDue());
        assertTrue(todo.getStatus());
        todo.setStatus(0);
        assertFalse(todo.getStatus());
        todo.printTodo(1);
    }

    @Test
    public void testTodoEqualsAndHashCode() {
        todo = new SubTodo("test", "test", true);
        assertEquals(todo.getType(), "Sub");
        Map<String, Todo> map = new HashMap<>();
        map.put("subtodo1",todo);
        SubTodo todo2 = new SubTodo("test", "test", false);
        SubTodo todo3 = new SubTodo("test", "test", true);
        assertFalse(map.containsValue(todo2));
        assertTrue(map.containsValue(todo3));
        assertTrue(map.get("subtodo1").equals(todo));
        todo.hashCode();
    }


}
