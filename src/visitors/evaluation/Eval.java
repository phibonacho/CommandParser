package visitors.evaluation;

import RMIForum.Broker.BrokerClass;
import parser.TokenType;
import parser.ast.Ident;
import parser.ast.Stmt;
import parser.ast.StmtSeq;
import visitors.Visitors;

import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.List;

public class Eval implements Visitors<Value> {

    private BrokerClass broker = null;

    {
        try {
            broker = new BrokerClass();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        System.err.println("Successfully init");
    }

    @Override
    public Value visitAdd(String m, String t) {
        // add topics:
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
                broker.PublishRequest(m, t);
            } catch (RemoteException e) {
                e.printStackTrace();
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
                broker.kickUser(l);
                break;
        }
        return null;
    }

    @Override
    public Value visitList(TokenType t, Ident o) {
        System.out.println("Visiting list statement, with tokentype: "+t);
        List<String> toList = null;
        switch (t){
            case TOPIC:
                try {
                    toList = broker.getTopics().ListTopicName();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case MESSAGE:
                try {
                    toList = broker.getTopics().getTopicNamed(o.getName()).ListMessages();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case USER:
                if(o == null) toList = broker.getConnectedgit Users();
                else {
                    try {
                        toList = broker.getTopics().getTopicNamed(o.getName()).ListUsers();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
        System.out.println("Exited switch");
        if(toList == null) System.out.println("No "+ t +" available");
        else for(String it : toList) System.out.println(it);
        return null;
    }

    @Override
    public Value visitUnsubscribe(Ident Topic) {
        try {
            broker.Unsubscribe(Topic.getName());
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
            broker.ConnectionRequest(ip, username);
        } catch (RemoteException e) {
            System.err.println("Cannot connect: "+e.getMessage());
        }
        return null;
    }

    @Override
    public Value visitDisconnect() {
        broker.Disconnect();
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
            broker.Subscribe(Topic.getName());
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
        return null;
    }

    @Override
    public Value visitStart(String ip) {
        broker.start(ip);
        return null;
    }
}
