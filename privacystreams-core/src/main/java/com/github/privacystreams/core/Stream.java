package com.github.privacystreams.core;

import android.content.Context;

import org.json.JSONObject;
import com.github.privacystreams.core.utils.Logging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * Created by yuanchun on 28/11/2016.
 * Stream is one of the essential classes used in PrivacyStreams.
 * Most personal data access/process operation in PrivacyStreams use Stream as the intermediate.
 *
 * A Stream is consist of one or multiple items.
 * The items are produced by MultiItemStreamProvider functions (like LocationUpdatesProvider, CallLogProvider, etc.),
 * transformed by M2MTransformation functions (like filter, reorder, map, etc.),
 * and outputted by ItemsFunction functions (like print, toList, etc.).
 *
 * Stream producer functions (including MultiItemStreamProvider and M2MTransformation)
 * should make sure the stream is not closed before writing items to it, using:
 *      stream.isClosed()
 * Stream consumer functions (including M2MTransformation and ItemsFunction)
 * should stop reading from Stream if the stream is ended.
 *      If stream.read() returns a null, it means the stream is ended.
 */

public abstract class Stream {
    private final BlockingQueue<Item> dataQueue;
    private final Context context;
    private final UQI uqi;

    private volatile boolean isClosed = false;
    private volatile boolean isEmpty = false;

    Stream(UQI uqi) {
        this.uqi = uqi;
        this.context = uqi.getContext();
        this.dataQueue = new LinkedBlockingQueue<>();
    }

    /**
     * Write an item to the stream,
     * or write a null to end the stream.
     * @param item  the item to write to the stream, null indicates the end of the stream
     */
    public void write(Item item) {
        if (this.isClosed) {
//            Logging.warn("Writing to a closed stream!");
            return;
        }
        if (item == null) {
            item = Item.EOS;
        }
        try {
            dataQueue.put(item);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read an item from the stream,
     * if the item is null, it means the stream is ended.
     * The method might block if the stream has no item but is not ended.
     * @return the item read from the stream, or null meaning end of stream
     */
    public Item read() {
        if (this.isEmpty) {
            Logging.warn("Reading from a empty stream!");
        }
        try {
            Item item = this.dataQueue.take();
            if (item != Item.EOS) return item;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.isEmpty = true;
        return null;
    }

    /**
     * Read all items from the stream to a List,
     * The method blocks until the stream is ended.
     * @return the List of items read from the stream
     */
    public List<Item> readAll() {
        List<Item> items = new ArrayList<>();
        while (true) {
            Item item = this.read();
            if (item == null) break;
            items.add(item);
        }
        return items;
    }

    /**
     * Check whether the stream is closed,
     * Stream generator functions should make sure the stream is not closed this writing items to it.
     * @return true if the stream is closed, meaning the stream does not accept new items
     */
    public boolean isClosed() {
        return this.isClosed;
    }

    public abstract Function<Void, ? extends Stream> getStreamProvider();

    /**
     * Close the stream
     * By closing the stream, it does not accept new items from the MultiItemStreamProvider any more.
     */
    public void close() {
        this.isClosed = true;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> outputMap = new HashMap<>();
        outputMap.put("streamProvider", this.getStreamProvider().toString());
        return outputMap;
    }

    public JSONObject toJson() {
        return new JSONObject(this.toMap());
    }

    public String toString() {
        return this.toMap().toString();
    }

    public Context getContext() {
        return context;
    }

    public UQI getUQI() {
        return this.uqi;
    }
}