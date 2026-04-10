package net.sfdc.ci;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

class RandomSongShuffler {
    private ArrayList<Integer> songsList = new ArrayList<>();
    private LinkedList<Integer> songsOutOfRotation = new LinkedList<>();
    private int k;

    public RandomSongShuffler(int size) {
        this.k = size;
    }

    public void addSong(int song) {
        songsList.add(song);
    }

    public int selectSong() {
        Random random = new Random();
        int size = songsList.size();
        System.out.println("Songs list size: " + size + ", songsOutOfRotation: " + songsOutOfRotation.size());
        int songIdx = random.nextInt(size);
        int songSelected = songsList.get(songIdx);
        songsList.set(songIdx, songsList.get(size-1));
        songsList.remove(size-1);
        songsOutOfRotation.addLast(songSelected);
        if(songsOutOfRotation.size() > k) {
            songsList.add(songsOutOfRotation.removeFirst());
        }

        return songSelected;
    }
}
public class Main {
    public static void main(String[] args) {
        RandomSongShuffler shuffler = new RandomSongShuffler(2);
        shuffler.addSong(4);
        shuffler.addSong(3);
        shuffler.addSong(5);

        System.out.println("First song: " + shuffler.selectSong());
        System.out.println("Second song: " + shuffler.selectSong());
        System.out.println("Third song: " + shuffler.selectSong());
        System.out.println("Fourth song: " + shuffler.selectSong());
        System.out.println("Fifth song: " + shuffler.selectSong());
        System.out.println("Sixth song: " + shuffler.selectSong());
        Scanner sc = new Scanner(System.in);
        sc.nextLine();
    }
}
