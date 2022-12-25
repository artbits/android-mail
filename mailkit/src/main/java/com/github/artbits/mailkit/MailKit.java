package com.github.artbits.mailkit;

import android.os.Handler;
import android.os.Looper;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public final class MailKit {

    static Handler handler = new Handler(Looper.getMainLooper());
    static ExecutorService thread = Executors.newCachedThreadPool();


    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> thread.shutdownNow()));
    }


    public static class Config {
        public String account;
        public String password;
        public String nickname;
        public String SMTPHost;
        public String IMAPHost;
        public Integer SMTPPort;
        public Integer IMAPPort;
        public boolean SMTPSSLEnable;
        public boolean IMAPSSLEnable;

        public Config(Consumer<Config> consumer) {
            consumer.accept(this);
        }
    }


    public static class SMTP extends SMTPService {
        public SMTP(Config config) {
            super(config);
        }
    }


    public static class IMAP extends IMAPService {
        public IMAP(Config config) {
            super(config);
        }

        public static class Folder extends MailFolder {
            protected Folder(Config config, String folderName) {
                super(config, folderName);
            }
        }

        public static class Inbox extends MailFolder {
            protected Inbox(Config config, String folderName) {
                super(config, folderName);
            }
        }

        public static class DraftBox extends MailFolder {
            protected DraftBox(Config config, String folderName) {
                super(config, folderName);
            }

            @Override
            public void save(Draft draft, Runnable runnable, Consumer<Exception> consumer) {
                super.save(draft, runnable, consumer);
            }
        }
    }


    public static class Draft {
        public String[] to;
        public String[] cc;
        public String[] bcc;
        public String subject;
        public String text;
        public String html;

        public Draft(Consumer<Draft> consumer) {
            consumer.accept(this);
        }
    }


    public static class Msg {
        public long uid;
        public long sentDate;
        public String subject;
        public Flags flags;
        public From from;
        public List<To> toList;
        public List<Cc> ccList;
        public MainBody mainBody;

        protected Msg(Consumer<Msg> consumer) {
            consumer.accept(this);
        }

        public static class From {
            public String address;
            public String nickname;

            From(Consumer<From> consumer) {
                consumer.accept(this);
            }
        }

        public static class To {
            public String address;
            public String nickname;

            To(Consumer<To> consumer) {
                consumer.accept(this);
            }
        }

        public static class Cc {
            public String address;
            public String nickname;

            Cc(Consumer<Cc> consumer) {
                consumer.accept(this);
            }
        }

        public static class Flags {
            public boolean isSeen;
            public boolean isStar;

            Flags(Consumer<Flags> consumer) {
                consumer.accept(this);
            }
        }

        public static class MainBody {
            public String type;
            public String content;

            MainBody(Consumer<MainBody> consumer) {
                consumer.accept(this);
            }
        }
    }


    public static void auth(Config config, Runnable runnable, Consumer<Exception> consumer) {
        new AuthService(config).auth(runnable, consumer);
    }

}