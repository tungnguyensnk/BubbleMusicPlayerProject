package com.example.musicplaydemo;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainView implements Initializable {
    public AnchorPane root;
    public Circle circle;
    public ImageView playIcon;
    public ImageView pauseIcon;
    public AnchorPane menuPane;
    public ImageView imageView;
    public Label label;
    public ImageView truocIcon;
    public ImageView sauIcon;
    public AnchorPane mainPane;
    public ImageView xIcon;
    public Label updateLabel;
    public TextField getLinkLabel;
    public Label timeCur;
    public Label timeFull;
    public Label linkPhatHien;
    public AnchorPane getPane;

    boolean isPlaying = false;
    boolean isOpened = false;
    boolean onCircle = false;
    boolean isClickedPause = false;
    boolean onMenu = false;
    int curSong = 0;
    int curTime = 0;

    String linkChrome = "";

    Player player = new Player("D:\\Java\\MusicPlayDemo\\data\\n.wav");

    ArrayList<String> arrayList = new ArrayList<>();
    ArrayList<String> listLink = new ArrayList<>();
    ArrayList<String> listKey = new ArrayList<>();
    ArrayList<String> blackList = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Files.createDirectories(Paths.get(System.getProperty("user.dir") + "/data"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        getLinkFromChrome();

        circle.setFill(new ImagePattern(new Image(Objects.requireNonNull(getClass().getResourceAsStream("z.jpg")))));
        circle.setRadius(60);

        playIcon.setVisible(false);
        pauseIcon.setVisible(false);

        menuPane.setVisible(false);
        getPane.setVisible(false);

        PauseTransition delay = new PauseTransition(Duration.millis(100));
        delay.setOnFinished(event -> {
            Rectangle clip = new Rectangle(
                    imageView.getFitWidth(), imageView.getFitHeight()
            );
            clip.setArcWidth(80);
            clip.setArcHeight(80);
            imageView.setClip(clip);

            SnapshotParameters parameters = new SnapshotParameters();
            parameters.setFill(Color.TRANSPARENT);
            WritableImage image = imageView.snapshot(parameters, null);

            imageView.setClip(null);
            imageView.setImage(image);
        });
        delay.play();

        Task<Void> task = new Task<>() {
            @Override
            public Void call() throws InterruptedException {
                for (int i = 0; i < 1000000; i++) {
                    Runnable task1 = () -> Platform.runLater(() -> updateSong());
                    Thread thread1 = new Thread(task1);
                    thread1.setDaemon(true);
                    thread1.start();
                    Thread.sleep(5000);
                }
                return null;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    public void updateSong() {
        try {
            File[] listFile = new File(System.getProperty("user.dir") + "/data").listFiles();
            assert listFile != null;
            for (File file : listFile) {
                if (file.getAbsolutePath().contains(".mp3") || file.getAbsolutePath().contains(".MP3") ||
                        file.getAbsolutePath().contains(".wav") || file.getAbsolutePath().contains(".WAV")) {
                    boolean found = false;
                    for (String s : arrayList) {
                        if (file.getAbsolutePath().equals(s)) {
                            found = true;
                            break;
                        }
                    }

                    if (!found)
                        arrayList.add(file.getAbsolutePath());
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    public void pMusic() {
        if (!isOpened) {
            playSong();
            return;
        }
        if (isPlaying) {
            playIcon.setVisible(true);
            pauseIcon.setVisible(false);
            isPlaying = false;
            isClickedPause = true;
        } else {
            playIcon.setVisible(false);
            pauseIcon.setVisible(true);
            isPlaying = true;

            Task<Void> task2 = new Task<>() {
                @Override
                public Void call() {
                    changeCircle();
                    return null;
                }
            };
            new Thread(task2).start();
        }
        player.pause();
    }

    public void mouseExitMain() {
        onCircle = false;
        PauseTransition delay = new PauseTransition(Duration.millis(100));
        delay.setOnFinished(event -> {
            if (!onMenu && !onCircle)
                menuPane.setVisible(false);
        });
        delay.play();
        playIcon.setVisible(false);
        pauseIcon.setVisible(false);
    }

    public void mouseMoveMain() {
        if (!onCircle) {
            circle.setRadius(60);
            PauseTransition delay = new PauseTransition(Duration.millis(100));
            delay.setOnFinished(event -> menuPane.setVisible(true));
            delay.play();

            if (isPlaying)
                pauseIcon.setVisible(true);
            else
                playIcon.setVisible(true);
        }
        onCircle = true;
    }

    public void changeCircle() {
        for (int i = 0; i < 1000000000; i++) {
            if (!player.isRun()) {
                isPlaying = false;
                //playIcon.setVisible(true);
                //pauseIcon.setVisible(false);

                if (isClickedPause)
                    isClickedPause = false;
                else {
                    isOpened = false;
                    player.close();
                    sauClick();
                }
                break;
            }

            if ((i + 1) % 50 == 0) {
                curTime++;
                updateTime();
            }

            circle.setRadius(60);
            if (!onCircle && !onMenu) {
                Runnable task1 = () -> Platform.runLater(() -> circle.setRadius(60 + player.getData() * 15));
                Thread thread1 = new Thread(task1);
                thread1.setDaemon(true);
                thread1.start();
            }

            try {
                Thread.sleep(19);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateTime() {
        int gio = curTime / 60;
        String gioS, phutS;

        if (gio > 9)
            gioS = gio + "";
        else
            gioS = "0" + gio;

        int phut = curTime - gio * 60;

        if (phut > 9)
            phutS = phut + "";
        else
            phutS = "0" + phut;
        PauseTransition delay = new PauseTransition(Duration.millis(10));
        delay.setOnFinished(event -> timeCur.setText(gioS + ":" + phutS));
        delay.play();
    }

    public void thoat() {
        System.exit(0);
    }

    public void truocClick() {
        if (curSong == 0)
            curSong = arrayList.size() - 1;
        else
            curSong--;
        playSong();

    }

    public void sauClick() {
        if (curSong == arrayList.size() - 1)
            curSong = 0;
        else
            curSong++;
        playSong();
    }

    private void playSong() {
        curTime = 0;
        updateTime();
        player.close();
        player = new Player(arrayList.get(curSong));
        player.open();
        Task<Void> task9 = new Task<>() {
            @Override
            public Void call() {
                changeCircle();
                return null;
            }
        };
        new Thread(task9).start();

        Runnable task2 = () -> Platform.runLater(() -> {
            if (player.getTitle().trim().length() == 0)
                label.setText("No Title");
            else
                label.setText(player.getTitle());

            timeFull.setText("/" + player.getFulltime());
        });
        Thread thread2 = new Thread(task2);
        thread2.setDaemon(true);
        thread2.start();

        playIcon.setVisible(false);
        pauseIcon.setVisible(true);
        isPlaying = true;
        isOpened = true;
    }

    public void mouseDragged() {
        PauseTransition delay = new PauseTransition(Duration.millis(100));
        delay.setOnFinished(event -> menuPane.setVisible(false));
        delay.play();
    }

    public void mouseExitMenu() {
        onMenu = false;

        PauseTransition delay = new PauseTransition(Duration.millis(100));
        delay.setOnFinished(event -> {
            if (!onMenu && !onCircle)
                menuPane.setVisible(false);
        });
        delay.play();
    }

    public void mouseMoveMenu() {
        onMenu = true;
    }

    public void getLink() {
        Task<Void> task = new Task<>() {
            @Override
            public Void call() throws InterruptedException {
                //lay link youtube tu playlist
                Runnable task1 = () -> Platform.runLater(() -> updateLabel.setText("Đang lấy link..."));
                Thread thread1 = new Thread(task1);
                thread1.setDaemon(true);
                thread1.start();

                String linkGot = getLinkLabel.getText().trim();
                String[] listVideo = {""};
                if (linkGot.contains("youtube.com/playlist?list")) {
                    HttpResponse<String> response = Unirest.get("https://ph.tinhtong.vn/Home/GetLinkVideoOfYoutubePlaylist?link=" + linkGot)
                            .asString();

                    String res = response.getBody();
                    res = res.substring(res.indexOf("<tbody>") + 7, res.indexOf("</tbody>")).trim();

                    listVideo = res.split("<td class=\"linkCol\"><a href=\"");
                } else if (linkGot.contains("watch?") || linkGot.contains("youtu.be")) {
                    listVideo[0] = linkGot + "\">";
                } else
                    return null;
                int dem = 0;
                for (String linkVideo : listVideo) {

                    System.out.println(linkVideo);
                    if (linkVideo.charAt(0) == '<')
                        continue;
                    dem++;
                    //tach link
                    linkVideo = linkVideo.substring(0, linkVideo.indexOf("\">"));
                    //convert sang mp3
                    HttpResponse<String> responsetmp = Unirest.get("https://loader.to/ajax/download.php?start=1&end=1&format=mp3&url=" + linkVideo)
                            .asString();
                    String key = responsetmp.getBody();
                    //lay key de getlink download
                    key = key.substring(key.indexOf("\"id\":\"") + 6, key.indexOf("\","));
                    listKey.add(key);
                    int finalDem = dem;
                    Runnable task2 = () -> Platform.runLater(() -> updateLabel.setText("Đang lấy key " + finalDem + "..."));
                    Thread thread2 = new Thread(task2);
                    thread2.setDaemon(true);
                    thread2.start();
                }
                Runnable task3 = () -> Platform.runLater(() -> updateLabel.setText("Đang đợi server xử lý..."));
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Thread thread3 = new Thread(task3);
                thread3.setDaemon(true);
                thread3.start();

                Thread.sleep(2000);

                for (int i = 0; i < 40; i++) {
                    HttpResponse<String> responsee = Unirest.get("https://loader.to/ajax/progress.php?id=" + listKey.get(listKey.size() - 1))
                            .asString();

                    String progress = responsee.getBody();
                    progress = progress.substring(progress.indexOf("\"progress\":") + 11, progress.indexOf(",\"download"));

                    String finalProgress = progress;
                    Runnable task = () -> Platform.runLater(() -> updateLabel.setText("Đang đợi server xử lý..." + Integer.parseInt(finalProgress) / 10 + "%"));
                    Thread thread = new Thread(task);
                    thread.setDaemon(true);
                    thread.start();

                    if (!responsee.getBody().contains("success\":\"0\""))
                        break;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                Thread.sleep(3000);
                Runnable task4 = () -> Platform.runLater(() -> updateLabel.setText("Tải dữ liệu danh sách..."));
                Thread thread4 = new Thread(task4);
                thread4.setDaemon(true);
                thread4.start();

                for (int i = 0; i < listKey.size(); i++) {
                    HttpResponse<String> responsee = Unirest.get("https://loader.to/ajax/progress.php?id=" + listKey.get(i))
                            .asString();

                    System.out.println("check");
                    if (responsee.getBody().contains("success\":\"0\"")) {
                        i--;
                        continue;
                    }

                    System.out.println("ok");
                    String urltmp = responsee.getBody();
                    urltmp = urltmp.substring(urltmp.indexOf("url\":\"") + 6, urltmp.indexOf("\",\"text\":\"Fin")).replace("\\", "");
                    System.out.println(urltmp);

                    listLink.add(urltmp);
                }
                Runnable task5 = () -> Platform.runLater(() -> updateLabel.setText("Xong!"));
                Thread thread5 = new Thread(task5);
                thread5.setDaemon(true);
                thread5.start();

                Runnable task6 = () -> Platform.runLater(() -> updateLabel.setText("Đang tải dữ liệu về..."));
                Thread thread6 = new Thread(task6);
                thread6.setDaemon(true);
                thread6.start();

                for (int i = 0; i < listLink.size(); i++) {
                    try {
                        URL url = new URL(listLink.get(i));
                        ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
                        FileOutputStream fileOutputStream = new FileOutputStream(System.getProperty("user.dir") + "/data/" + listKey.get(i) + ".mp3");
                        fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                        int finalI = i;
                        Runnable task7 = () -> Platform.runLater(() -> updateLabel.setText("Đang tải dữ liệu về..." + (finalI + 1) + "/" + listLink.size()));
                        Thread thread7 = new Thread(task7);
                        thread7.setDaemon(true);
                        thread7.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                listLink = new ArrayList<>();
                listKey = new ArrayList<>();

                Runnable task8 = () -> Platform.runLater(() -> updateLabel.setText(""));
                Thread thread8 = new Thread(task8);
                thread8.setDaemon(true);
                thread8.start();

                Runnable task9 = () -> Platform.runLater(() -> getLinkLabel.setText(""));
                Thread thread9 = new Thread(task9);
                thread9.setDaemon(true);
                thread9.start();
                linkChrome = "";
                return null;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    public void getLinkFromChrome() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws InterruptedException {
                for (int i = 0; i < 1000000000; i++) {
                    Connection connection = null;
                    try {
                        try (FileChannel sourceChannel = new FileInputStream("C://Users//" + System.getProperty("user.name") + "//AppData//Local//Google//Chrome//User Data//Default//History").getChannel();
                             FileChannel destChannel = new FileOutputStream(System.getProperty("user.dir") + "//History").getChannel()) {
                            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
                        }
                        connection = DriverManager.getConnection("jdbc:sqlite:" + System.getProperty("user.dir") + "//History");
                        Statement statement = connection.createStatement();
                        statement.setQueryTimeout(30);

                        ResultSet rs = statement.executeQuery("SELECT * FROM urls ORDER BY id DESC  LIMIT 10");
                        while (rs.next()) {
                            String linktmp = rs.getString(2);
                            if (linktmp.contains("youtube.com/watch?") && !blackList.contains(linktmp)) {
                                String tmp = rs.getString(3);
                                linkChrome = linktmp;
                                Runnable task5 = () -> Platform.runLater(() -> {
                                    linkPhatHien.setText(tmp);
                                    getPane.setVisible(true);
                                });
                                Thread thread5 = new Thread(task5);
                                thread5.setDaemon(true);
                                thread5.start();
                                break;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (connection != null)
                                connection.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    Thread.sleep(1000);
                }
                return null;
            }
        };

        new Thread(task).start();
    }

    public void taiXuong() {
        Runnable task9 = () -> Platform.runLater(() -> {
            menuPane.setVisible(true);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            getLinkLabel.setText(linkChrome);
            getPane.setVisible(false);
            getLink();
            blackList.add(linkChrome);
        });
        Thread thread9 = new Thread(task9);
        thread9.setDaemon(true);
        thread9.start();
    }

    public void anDi() {
        blackList.add(linkChrome);
        linkChrome = "";
        getPane.setVisible(false);
    }
}
