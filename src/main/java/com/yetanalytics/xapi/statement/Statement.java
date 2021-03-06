package com.yetanalytics.xapi.statement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import com.google.gson.Gson;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import com.nimbusds.langtag.LangTag;
import org.joda.time.Duration;


/**
 * Created by Jason Lewis on 10/20/14.
 * Describes the xAPI Statement object as defined in the xAPI Spec v1.0.2
 * Spec available at https://github.com/adlnet/xAPI-Spec/blob/master/xAPI.md
 */
public class Statement {
    public UUID id;
    public IActor actor;
    public Verb verb;
    public IStatementObject object;
    public Result result;
    public StatementContext context;

    public String timestamp;

    protected String version = "1.0.3";


    /**
     * Constructor for a new Statement with the minimum required elements
     *
     * @param act   An object (i.e., Agent or Group) implementing the IActor interface
     * @param v     A Verb object
     * @param obj   An Object of the Statement implementing the IObject interface
     */
    public Statement(IActor act, Verb v, IStatementObject obj) {
        id = UUID.randomUUID();
        actor = act;
        verb = v;
        object = obj;
        timestamp = DateTime.now(DateTimeZone.UTC).toString();
    }

    public String toJSONString() {
        Gson gson = new Gson();

        return gson.toJson(this);
    }

    /**
     *
     * @param res   A Result object conforming to xAPI spec 4.1.5 Result
     * @return      boolean - true if the Result was successfully added to the Statement
     */
    public boolean setResult(Result res) {
        result = res;
        return true;
    }

    public boolean setResultResponse(String resp) {
        result = new Result(resp);
        return true;
    }

    /**
     *
     * @return      The Result object associated with the statement
     */
    public Result getResult() {
        if(this.result != null) {
            return this.result;
        } else {
            return null;
        }
    }



    public class StatementReference implements IStatementObject {
        protected static final String objectType = "StatementRef";
        protected UUID id;

        /**
         * StatementReference is a pointer to an existing statement
         * @param srid  The UUID of an existing statement
         */
        protected StatementReference(UUID srid) {
            id = srid;
        }

        protected StatementReference(String srid) {
            id = UUID.fromString(srid);
        }

        /**
         *
         * @return objectType field, in this case always "StatementRef"
         */
        @Override
        public String objectType() {
            return objectType;
        }
    }



    /**
     * The 'Result' for the Statement.
     *
     * @wip
     */
    private static final class Result {
        public boolean success;
        public boolean completion;
        public String response;
        public Duration duration;
        public Score score;

        public Result(String resp) {
            response = resp;
        }

        public Result(boolean success, boolean completion, String response, Duration duration, Score score) {
            System.out.println("Fuck this. Fuck all this noise");
        }

        /**
         * The 'Score' object for the 'Result' object.
         *
         * @wip
         */
        private class Score {
            public int scaled;
            public int max;
            public int min;
            public int raw;

            public Score(int scaled, int max, int min, int raw){
                try {
                    checkScaledRange(scaled);
                } catch(RuntimeException e) {
                    e.printStackTrace();
                }
                assert(max > min);
            }

            /**
             * checks that the value of 'scaled' is within valid parameters, -1..1
             * Throws an exception if our of bounds.
             * @param sc    The 'scaled' value, which should be an integer between -1 and 1
             * @return      The checked scaled value
             */
            private int checkScaledRange(int sc) {
                if ((sc < -1) || (sc > 1)) {
                    throw new RuntimeException("scaled value is out of bounds");
                } else
                    return sc;
            }
        }
    }
}
