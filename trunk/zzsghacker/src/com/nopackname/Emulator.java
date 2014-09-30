package com.nopackname;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.nopackname.response.API_MY_GEN_MOD_REINIT_BASE_RESPONSE;
import com.nopackname.response.API_MY_GEN_MOD_REINIT_RESPONSE;
import com.nopackname.response.API_MY_GEN_MOD_WASHPRICE_BASE_RESPONSE;
import com.nopackname.response.API_MY_GEN_MOD_WASHPRICE_RESPONSE;
import com.nopackname.tools.HttpClient;
import com.nopackname.tools.HttpResult;
import com.nopackname.tools.JSONUtil;

public class Emulator {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // constant

        // init
        int myYuanshaoId = 378933;
        int myCity = 91739;

        // target
        int pcTarget = -1;
        int piTarget = -1;
        int pwTarget = -1;
        int currentPc = 999;
        int currentPi = 999;
        int currentPw = 0;
        int currentPcMax = 0;
        int currentPiMax = 0;
        int currentPwMax = 0;
        Queue<API> commands = new ConcurrentLinkedQueue<API>();
        commands.add(new API_GET_USERINFO());
        commands.add(new API_MY_GEN_MOD(API_MY_GEN_MOD.WASHPRICE, myYuanshaoId, myCity));
        commands.add(new API_MY_GEN_MOD(API_MY_GEN_MOD.REINIT, myYuanshaoId, myCity, API_MY_GEN_MOD.MODE_EXP));

        // do command
        while (true) {
            API cmd = commands.poll();
            if (null == cmd) {
                break;
            }
            String uriString = API.HOST_MAIN + cmd.toString();
            Map<String, String> headers = new HashMap<String, String>();
            final HttpResult result = new HttpResult();
            try {
                HttpClient.httpGet(uriString, headers, result, API.generateAccLgoinResponseHandler(result));
            } catch (Exception e) {
                // TODO: handle exception
            }
            if (cmd instanceof API_MY_GEN_MOD) {
                // logical
                API_MY_GEN_MOD gen = (API_MY_GEN_MOD) cmd;
                if (API_MY_GEN_MOD.REINIT.equals(gen.action)) {
                    API_MY_GEN_MOD_REINIT_BASE_RESPONSE resp = (API_MY_GEN_MOD_REINIT_BASE_RESPONSE) JSONUtil
                            .parseObject(result.getResponse(), API_MY_GEN_MOD_REINIT_BASE_RESPONSE.class);
                    API_MY_GEN_MOD_REINIT_RESPONSE reinit = resp.getRet();
                    // judgement
                    if (pcTarget > 0) {
                        if (reinit.getCt() > currentPc) {
                            commands.add(new API_MY_GEN_MOD(API_MY_GEN_MOD.ACCEPT, myYuanshaoId, myCity,
                                    API_MY_GEN_MOD.MODE_EXP));
                        } else {
                            commands.add(new API_MY_GEN_MOD(API_MY_GEN_MOD.REJECT, myYuanshaoId, myCity,
                                    API_MY_GEN_MOD.MODE_EXP));
                        }
                        currentPc = reinit.getCt();
                        if (currentPc < currentPcMax - 10) {
                            commands.add(new API_MY_GEN_MOD(API_MY_GEN_MOD.REINIT, myYuanshaoId, myCity,
                                    API_MY_GEN_MOD.MODE_EXP));
                        }
                    } else if (piTarget > 0) {
                        System.out.println("now jueji: " + reinit.getIt() + " pre jueji: " + currentPi);
                        if (reinit.getIt() > currentPi) {
                            commands.add(new API_MY_GEN_MOD(API_MY_GEN_MOD.ACCEPT, myYuanshaoId, myCity,
                                    API_MY_GEN_MOD.MODE_EXP));
                            currentPi = reinit.getIt();
                        } else {
                            commands.add(new API_MY_GEN_MOD(API_MY_GEN_MOD.REJECT, myYuanshaoId, myCity,
                                    API_MY_GEN_MOD.MODE_EXP));
                        }
                        if (currentPi < currentPiMax - 10) {
                            commands.add(new API_MY_GEN_MOD(API_MY_GEN_MOD.REINIT, myYuanshaoId, myCity,
                                    API_MY_GEN_MOD.MODE_EXP));
                        }
                    } else if (pwTarget > 0) {
                        if (reinit.getWt() > currentPw) {
                            commands.add(new API_MY_GEN_MOD(API_MY_GEN_MOD.ACCEPT, myYuanshaoId, myCity,
                                    API_MY_GEN_MOD.MODE_EXP));
                        } else {
                            commands.add(new API_MY_GEN_MOD(API_MY_GEN_MOD.REJECT, myYuanshaoId, myCity,
                                    API_MY_GEN_MOD.MODE_EXP));
                        }
                        currentPw = reinit.getWt();
                        if (currentPw < currentPwMax - 10) {
                            commands.add(new API_MY_GEN_MOD(API_MY_GEN_MOD.REINIT, myYuanshaoId, myCity,
                                    API_MY_GEN_MOD.MODE_EXP));
                        }
                    }
                } else if (API_MY_GEN_MOD.REJECT.equals(gen.action)) {

                } else if (API_MY_GEN_MOD.ACCEPT.equals(gen.action)) {

                } else if (API_MY_GEN_MOD.WASHPRICE.equals(gen.action)) {
                    API_MY_GEN_MOD_WASHPRICE_BASE_RESPONSE baseResp = (API_MY_GEN_MOD_WASHPRICE_BASE_RESPONSE) JSONUtil
                            .parseObject(result.getResponse(), API_MY_GEN_MOD_WASHPRICE_BASE_RESPONSE.class);
                    API_MY_GEN_MOD_WASHPRICE_RESPONSE resp = baseResp.getRet();
                    currentPc = resp.getPc();
                    currentPcMax = resp.getPcmax();
                    if (currentPc < currentPcMax - 10) {
                        pcTarget = currentPcMax - 10;
                    }
                    currentPi = resp.getPi();
                    currentPiMax = resp.getPimax();
                    if (currentPi < currentPiMax - 10) {
                        piTarget = currentPiMax - 10;
                    }
                    currentPw = resp.getPw();
                    currentPwMax = resp.getPwmax();
                    if (currentPw < currentPwMax - 10) {
                        pwTarget = currentPwMax - 10;
                    }
                }
            } else if (cmd instanceof API_GEN_CONSCRIBE) {

            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        System.out.println("Finished.");
    }
}
