package visitors.evaluation;

import RMIForum.Broker.BrokerClass;
import parser.TokenType;
import parser.ast.*;
import visitors.Visitors;

import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;

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
        if(!getMode()){
            System.err.println("You can't add topics in server mode...");
            return null;
        }
        if(m==null){
            try {
                if(broker.AddTopicRequest(t)) System.err.println("Successfully added");
                else System.err.println("Cannot add topic, maybe you've been removed from remote?");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        else{
            // publish message:
            try {
                if(!broker.PublishRequest(m, t))System.err.println("[Notification]: You must be subscribe to "+t+" if you want to send messages.");
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
                if(broker.removeMessage(t.getName(), l))System.err.println(t.getName()+" has been removed");
                else System.err.println("Impossible to remove "+t.getName());
                break;
            case TOPIC:
                if(broker.removeTopic(t.getName()))System.err.println(t.getName()+" has been removed");
                break;
            case USER:
                if(usermode) System.err.println("Yopu can't delete user in User Mode...");
                else{
                    try {
                        broker.ManualkickUser(l);
                    } catch (ExecutionException e1) {
                        e1.printStackTrace();
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
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
                    if(!getMode()) toList = broker.getTopics().ListTopicName();
                    else toList = broker.getServerTopics().ListTopicName();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case MESSAGE:
                try {
                    if(!getMode()) toList = broker.getTopics().getTopicNamed(o.getName()).ListMessages();
                    else toList = broker.getServerTopics().getTopicNamed(o.getName()).ListMessages();
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (NullPointerException nse){
                    System.err.println("No topic named <"+o.getName()+"> found...");
                    return null;
                }
                break;
            case USER:
                if(o == null)
                    if(!getMode())toList = broker.getConnectedUsers();
                    else{
                        System.err.println("You can't list users in User Mode");
                        return null;
                    }
                else {
                    try {
                        toList = broker.getServerTopics().getTopicNamed(o.getName()).ListUsers();
                    } catch (NullPointerException e) {
                        System.err.println("No topic named <"+o.getName()+"> found..");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
        if(toList == null) System.err.println("No "+ t +" available");
        else for(String it : toList) System.err.println(it);
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
                System.err.println("Connection successful");
                if(!getMode()) {
                    Uprompt = (username + "@" + ip);
                    setMode(true);
                }
            }
            else System.err.println("Connection failed");
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
        if (broker.disconnect())
        if(getMode()) {
            Uprompt = null;
            setMode(false);
        }
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
            else{
                System.err.println("Successfully Subscribed");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Value visitHelp() {
        System.err.println("sintassi comandi: \n"

                + "start <IPaddress> \n"

                + "connect <IPaddress> as <username> \n"

                + "exit \n"

                + "disconnect \n"

                + "switchmode \n"

                + "subscribe <TopicLabel> \n"

                + "unsibscribe <TopicLabel>\n"

                + "add message \"<message>\" in <nameTopic> \n"

                + "add topic <nameTopic> \n"

                + "remove user <nameUser> \n"

                + "remove topic <nameTopic> \n"

                + "list message in <nameTopic> \n"

                + "list user in <TopicLabel>"

                + "list user \n"

                + "list topic \n");
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
        try {
            broker.shutDown();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Value visitStart(IP ip) {
        broker.start(ip.accept(this).toString());
        if(getMode()) setMode(false);
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
        setMode(!getMode());
        return null;
    }

    public String getPrompt(){
        return getMode()?Uprompt:Sprompt;
    }

    private boolean getMode(){
        return usermode && broker.getConnectionStatus();
    }

    public void setMode(boolean m){
        usermode = m;
    }
}
