package visitors.evaluation;

import RMIForum.Broker.BrokerClass;
import parser.TokenType;
import parser.ast.*;
import visitors.Visitors;

import java.net.UnknownHostException;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.NoSuchElementException;

public class Eval implements Visitors<Value> {
    private String Sprompt = System.getProperty("user.name");
    private String Uprompt;
    private boolean usermode = false;
    private BrokerClass broker = null;

    {
        try {
            broker = new BrokerClass();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Value visitAdd(String m, String t) {
        // add topics:
        if(!usermode){
            System.err.println("You can't add topics in server mode...");
            return null;
        }
        if(m==null){
            try {
                broker.AddTopicRequest(t);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        else{
            // publish message:
            try {
                if(!broker.PublishRequest(m, t))System.err.println("You must be subscribe to "+t+" if you want to send messages.");
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (NoSuchElementException nse){
                System.err.println("No topic named <"+t+">");
            }
        }
        return null;
    }

    @Override
    public Value visitRemove(TokenType e, String l, Ident t) {
        switch (e){
            case MESSAGE:
                broker.removeMessage(t.getName(), l);
                break;
            case TOPIC:
                broker.removeTopic(t.getName());
                break;
            case USER:
                if(usermode) System.err.println("Yopu can't delete user in User Mode...");
                else broker.kickUser(l);
                break;
        }
        return null;
    }

    @Override
    public Value visitList(TokenType t, Ident o) {
        List<String> toList = null;
        switch (t){
            case TOPIC:
                try {
                    if(!usermode) toList = broker.getTopics().ListTopicName();
                    else toList = broker.getServerTopics().ListTopicName();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case MESSAGE:
                try {
                    if(!usermode) toList = broker.getTopics().getTopicNamed(o.getName()).ListMessages();
                    else toList = broker.getServerTopics().getTopicNamed(o.getName()).ListMessages();
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (NoSuchElementException nse){
                    System.err.println("No topic named <"+o.getName()+"> found...");
                    return null;
                }
                break;
            case USER:
                if(o == null)
                    if(!usermode)toList = broker.getConnectedUsers();
                    else{
                        System.err.println("You can't list users in User Mode");
                        return null;
                    }
                else {
                    try {
                        toList = broker.getServerTopics().getTopicNamed(o.getName()).ListUsers();
                    } catch (NoSuchElementException e) {
                        System.err.println("No topic named <"+o.getName()+"> found..");
                    }
                }
                break;
        }
        if(toList == null) System.out.println("No "+ t +" available");
        else for(String it : toList) System.out.println(it);
        return null;
    }

    @Override
    public Value visitUnsubscribe(Ident Topic) {
        try {
            broker.SubscribeRequest(Topic.getName(), "unsubscribe");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Value visitIdent(String id) {
        return null;
    }

    @Override
    public Value visitConnect(String ip, String username) {
        try {
            visitDisconnect();
            if(broker.ConnectionRequest(ip, username)){
                Uprompt=(username+"@"+ip);
                usermode = !usermode;
            }
        } catch (RemoteException e) {
            System.err.println("Cannot connect: "+e.getMessage());
            return null;
        } catch (IllegalArgumentException iae){
            System.err.println("No port available...");
        }
        return null;
    }

    @Override
    public Value visitDisconnect() {
        Uprompt = null;
        usermode = !usermode;
        broker.disconnect();
        return null;
    }

    @Override
    public Value visitProg(StmtSeq stmt) {
        stmt.accept(this);
        return null;
    }

    @Override
    public Value visitStmt(Stmt single) {
        single.accept(this);
        return null;
    }

    @Override
    public Value visitAddMessage(String message) {
        return null;
    }

    @Override
    public Value visitSubscribe(Ident Topic) {
        try {
            if(!broker.SubscribeRequest(Topic.getName(), "subscribe")){
                System.err.println("You can't subscribe...");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Value visitHelp() {
        return null;
    }

    @Override
    public Value visitMoreStmt(Stmt first, StmtSeq rest) {
        first.accept(this);
        rest.accept(this);
        return null;
    }

    @Override
    public Value visitExit() {
        visitDisconnect();
        broker.shutdown();
        return null;
    }

    @Override
    public Value visitStart(IP ip) {
        broker.start(ip.accept(this).toString());
        Sprompt= Sprompt+"@"+ip.getIp();
        return null;
    }

    @Override
    public Value visitIP(String ip) {
        return new StringValue(ip);
    }

    @Override
    public Value visitSwitch() {
        if(Uprompt==null){
            System.err.println("You must connect first in order to switch to user mode");
            return null;
        }
        usermode = !usermode;
        return null;
    }

    public String getPrompt(){
        return usermode?Uprompt:Sprompt;
    }
}
