/**
 * jsonata-java is the JSONata Java reference port
 * 
 * Copyright Dashjoin GmbH. https://dashjoin.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dashjoin.jsonata;

import com.dashjoin.jsonata.Jsonata.Frame;

import java.io.Serializable;

/**
 * Configure max runtime / max recursion depth.
 * See Frame.setRuntimeBounds - usually not used directly
 */
public class Timebox implements Serializable {

    long timeout = 5000L;
    int maxDepth = 100;

    long time = System.currentTimeMillis();
    int depth = 0;

    /**
     * Protect the process/browser from a runnaway expression
     * i.e. Infinite loop (tail recursion), or excessive stack growth
     *
     * @param {Object} expr - expression to protect
     * @param {Number} timeout - max time in ms
     * @param {Number} maxDepth - max stack depth
     */
    public Timebox(Frame expr) {
        this(expr, 5000L, 100);
    }

    public Timebox(Frame expr, long timeout, int maxDepth) {
        this.timeout = timeout;
        this.maxDepth = maxDepth;

        // register callbacks
        expr.setEvaluateEntryCallback( (_exp, _input, _env)-> {
            if (_env.isParallelCall) return;
            depth++;
            checkRunnaway();
        });
        expr.setEvaluateExitCallback( (_exp, _input, _env, _res)-> {
            if (_env.isParallelCall) return;
            depth--;
            checkRunnaway();
        });
    }

    void checkRunnaway() {
        if (depth > maxDepth) {
            // stack too deep
            throw new JException("Stack overflow error: Check for non-terminating recursive function.  Consider rewriting as tail-recursive. Depth="+depth+" max="+maxDepth,-1);
                //stack: new Error().stack,
                //code: "U1001"
            //};
        }
        if (System.currentTimeMillis() - time > timeout) {
            // expression has run for too long
            throw new JException("Expression evaluation timeout: Check for infinite loop",-1);
                //stack: new Error().stack,
                //code: "U1001"
            //};
        }
    };

}
