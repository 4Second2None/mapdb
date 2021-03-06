package org.mapdb.record;

import junit.framework.TestCase;
import org.mapdb.db.DB;
import org.mapdb.ser.Serializers;

public class VarRecordTest extends TestCase {

    DB db;
    VarRecord<String> ai;


    @Override
    protected void setUp() throws Exception {
        db = DB.Maker.memoryDB().make();
        ai = new VarRecord.Maker(db, "test", Serializers.STRING).init("test").make();
    }

    @Override
    protected void tearDown() throws Exception {
        db.close();
    }


    /*
     * constructor initializes to given value
     */
    public void testConstructor() {
        assertEquals("test", ai.get());
    }

    /*
     * default constructed initializes to empty string
     */
    public void testConstructor2() {
        VarRecord<String> ai = new VarRecord.Maker(db,"test2", Serializers.STRING).make();
        assertEquals(null, ai.get());
    }

    /*
     * get returns the last value set
     */
    public void testGetSet() {
        assertEquals("test", ai.get());
        ai.set("test2");
        assertEquals("test2", ai.get());
        ai.set("test3");
        assertEquals("test3", ai.get());

    }

    /*
     * compareAndSet succeeds in changing value if equal to expected else fails
     */
    public void testCompareAndSet(){
        assertTrue(ai.compareAndSet("test", "test2"));
        assertTrue(ai.compareAndSet("test2", "test3"));
        assertEquals("test3", ai.get());
        assertFalse(ai.compareAndSet("test2", "test4"));
        assertNotSame("test5", ai.get());
        assertTrue(ai.compareAndSet("test3", "test5"));
        assertEquals("test5", ai.get());
    }

    /*
     * compareAndSet in one thread enables another waiting for value
     * to succeed
     */
    public void testCompareAndSetInMultipleThreads() throws InterruptedException {
        Thread t = new Thread(new Runnable() {
            public void run() {
                while(!ai.compareAndSet("test2", "test3")) Thread.yield();
            }});

        t.start();
        assertTrue(ai.compareAndSet("test", "test2"));
        t.join(0);
        assertFalse(t.isAlive());
        assertEquals(ai.get(), "test3");
    }

    /*
     * getAndSet returns previous value and sets to given value
     */
    public void testGetAndSet(){
        assertEquals("test", ai.getAndSet("test2"));
        assertEquals("test2", ai.getAndSet("test3"));
        assertEquals("test3", ai.getAndSet("test4"));
    }

    /*
     * toString returns current value.
     */
    public void testToString() {
        assertEquals(ai.toString(), ai.get());
        assertEquals(ai.toString(), "test");
    }

}
