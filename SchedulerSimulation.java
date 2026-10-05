import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

class Colors {
    public static final String RESET = "\u001B[0m";
    public static final String BOLD = "\u001B[1m";
    public static final String CYAN = "\u001B[36m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String BLUE = "\u001B[34m";
    public static final String RED = "\u001B[31m";
    public static final String BG_BLUE = "\u001B[44m";
    public static final String BG_GREEN = "\u001B[42m";
    public static final String WHITE = "\u001B[37m";
    public static final String BRIGHT_WHITE = "\u001B[97m";
    public static final String BRIGHT_CYAN = "\u001B[96m";
    public static final String BRIGHT_YELLOW = "\u001B[93m";
    public static final String BRIGHT_GREEN = "\u001B[92m";
}

class Process implements Runnable {
    private String name; 
    private int burstTime; 
    private int timeQuantum; 
    private int remainingTime; 
    
    // FEATURE 1: Add priority field
    private int priority; 
    
    // FEATURE 3: Fields to track waiting time
    private long creationTime; 
    private long totalWaitingTime; 
    private long lastReadyTime; 

    public Process(String name, int burstTime, int timeQuantum, int priority) {
        this.name = name;
        this.burstTime = burstTime;
        this.timeQuantum = timeQuantum;
        this.remainingTime = burstTime; 
        this.priority = priority; 
        
        this.creationTime = System.currentTimeMillis(); 
        this.totalWaitingTime = 0; 
        this.lastReadyTime = this.creationTime; 
    }

    @Override
    public void run() {
        int runTime = Math.min(timeQuantum, remainingTime); 
        
        String quantumBar = createProgressBar(0, 15);
        System.out.println(Colors.BRIGHT_GREEN + "  ? " + Colors.BOLD + Colors.CYAN + name + 
                          Colors.RESET + Colors.GREEN + " executing quantum" + Colors.RESET + 
                          " [" + runTime + "ms] ");
        
        try {
            int steps = 5; 
            int stepTime = runTime / steps;
            
            for (int i = 1; i <= steps; i++) {
                Thread.sleep(stepTime);
                int quantumProgress = (i * 100) / steps;
                quantumBar = createProgressBar(quantumProgress, 15);
                System.out.print("\r  " + Colors.YELLOW + "?" + Colors.RESET + 
                                " Quantum progress: " + quantumBar);
            }
            System.out.println(); 
            
        } catch (InterruptedException e) {
            System.out.println(Colors.RED + "\n  ? " + name + " was interrupted." + Colors.RESET);
        }
        
        remainingTime -= runTime; 
        int overallProgress = (int) (((double)(burstTime - remainingTime) / burstTime) * 100);
        String overallProgressBar = createProgressBar(overallProgress, 20);
        
        System.out.println(Colors.YELLOW + "  ? " + Colors.CYAN + name + Colors.RESET + 
                          " completed quantum " + Colors.BRIGHT_YELLOW + runTime + "ms" + Colors.RESET + 
                          " ? Overall progress: " + overallProgressBar);
        System.out.println(Colors.MAGENTA + "     Remaining time: " + remainingTime + "ms" + Colors.RESET);
        
        if (remainingTime > 0) {
            System.out.println(Colors.BLUE + "  ? " + Colors.CYAN + name + Colors.RESET + 
                              " yields CPU for context switch" + Colors.RESET);
        } else {
            System.out.println(Colors.BRIGHT_GREEN + "  ? " + Colors.BOLD + Colors.CYAN + name + 
                              Colors.RESET + Colors.BRIGHT_GREEN + " finished execution!" + 
                              Colors.RESET);
        }
        System.out.println();
    }
    
    private String createProgressBar(int progress, int width) {
        int filled = (progress * width) / 100;
        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < width; i++) {
            if (i < filled) {
                bar.append(Colors.GREEN + "?" + Colors.RESET);
            } else {
                bar.append(Colors.WHITE + "?" + Colors.RESET);
            }
        }
        bar.append("] ").append(progress).append("%");
        return bar.toString();
    }

    public void runToCompletion() {
        try {
            System.out.println(Colors.BRIGHT_CYAN + "  ? " + Colors.BOLD + Colors.CYAN + name + 
                              Colors.RESET + Colors.BRIGHT_CYAN + " is the last process, running to completion" + 
                              Colors.RESET + " [" + remainingTime + "ms]");
            Thread.sleep(remainingTime); 
            remainingTime = 0; 
            System.out.println(Colors.BRIGHT_GREEN + "  ? " + Colors.BOLD + Colors.CYAN + name + 
                              Colors.RESET + Colors.BRIGHT_GREEN + " finished execution!" + Colors.RESET);
            System.out.println();
        } catch (InterruptedException e) {
            System.out.println(Colors.RED + "  ? " + name + " was interrupted." + Colors.RESET);
        }
    }

    public String getName() { return name; }
    public int getBurstTime() { return burstTime; }
    public int getRemainingTime() { return remainingTime; }
    public int getPriority() { return priority; } 
    public long getCreationTime() { return creationTime; }
    public long getTotalWaitingTime() { return totalWaitingTime; }
    public long getLastReadyTime() { return lastReadyTime; }
    
    public void updateWaitingTime() {
        long currentTime = System.currentTimeMillis();
        long waitTime = currentTime - lastReadyTime; 
        totalWaitingTime += waitTime;
    }
    
    public void setLastReadyTime(long time) { this.lastReadyTime = time; }
    public long getTurnaroundTime() { return totalWaitingTime + burstTime; }
    public boolean isFinished() { return remainingTime <= 0; }
}

public class SchedulerSimulation {
    
    // FEATURE 2: Static counter for context switches
    private static int contextSwitchCount = 0;
    
    // FEATURE 3: List to store all completed processes for summary
    private static List<Process> completedProcesses = new ArrayList<>();

    public static void main(String[] args) {
        // ?? هام: ضع رقمك الجامعي هنا
        int studentID = 447851247;  
        
        Random random = new Random(studentID);
        int timeQuantum = 2000 + random.nextInt(4) * 1000; 
        int numProcesses = 10 + random.nextInt(11); 
        
        Queue<Thread> processQueue = new LinkedList<>();
        Map<Thread, Process> processMap = new HashMap<>();
        
        System.out.println("\n" + Colors.BOLD + Colors.BRIGHT_CYAN + "????????????????????????????????????????????????????????????????????????????????????" + Colors.RESET);
        System.out.println(Colors.BOLD + Colors.BRIGHT_CYAN + "?" + Colors.RESET + Colors.BG_BLUE + Colors.BRIGHT_WHITE + Colors.BOLD + "                          CPU SCHEDULER SIMULATION                                " + Colors.RESET + Colors.BOLD + Colors.BRIGHT_CYAN + "?" + Colors.RESET);
        System.out.println(Colors.BOLD + Colors.BRIGHT_CYAN + "????????????????????????????????????????????????????????????????????????????????????" + Colors.RESET);
        System.out.println(Colors.BOLD + Colors.BRIGHT_CYAN + "?" + Colors.RESET + Colors.YELLOW + "  ? Processes:     " + Colors.RESET + Colors.BRIGHT_YELLOW + String.format("%-65s", numProcesses) + Colors.BOLD + Colors.BRIGHT_CYAN + "?" + Colors.RESET);
        System.out.println(Colors.BOLD + Colors.BRIGHT_CYAN + "?" + Colors.RESET + Colors.YELLOW + "  ? Time Quantum:  " + Colors.RESET + Colors.BRIGHT_YELLOW + String.format("%-65s", timeQuantum + "ms") + Colors.BOLD + Colors.BRIGHT_CYAN + "?" + Colors.RESET);
        System.out.println(Colors.BOLD + Colors.BRIGHT_CYAN + "?" + Colors.RESET + Colors.YELLOW + "  ?? Student ID:    " + Colors.RESET + Colors.BRIGHT_YELLOW + String.format("%-65s", studentID) + Colors.BOLD + Colors.BRIGHT_CYAN + "?" + Colors.RESET);
        System.out.println(Colors.BOLD + Colors.BRIGHT_CYAN + "????????????????????????????????????????????????????????????????????????????????????" + Colors.RESET + "\n");
        
        for (int i = 1; i <= numProcesses; i++) {
            int burstTime = timeQuantum/2 + random.nextInt(2 * timeQuantum + 1);
            int priority = 1 + random.nextInt(10); 
            Process process = new Process("P" + i, burstTime, timeQuantum, priority);
            addProcessToQueue(process, processQueue, processMap);
        }
        
        System.out.println(Colors.BOLD + Colors.GREEN + "????????????????????????????????????????????????????????????????????????????????????" + Colors.RESET);
        System.out.println(Colors.BOLD + Colors.GREEN + "?" + Colors.RESET + Colors.BG_GREEN + Colors.WHITE + Colors.BOLD + "                        ?  SCHEDULER STARTING  ?                               " + Colors.RESET + Colors.BOLD + Colors.GREEN + "?" + Colors.RESET);
        System.out.println(Colors.BOLD + Colors.GREEN + "????????????????????????????????????????????????????????????????????????????????????" + Colors.RESET + "\n");
        
        while (!processQueue.isEmpty()) {
            Thread currentThread = processQueue.poll(); 
            contextSwitchCount++;
            Process process = processMap.get(currentThread);
            process.updateWaitingTime();
            
            System.out.println(Colors.BOLD + Colors.MAGENTA + "?? Ready Queue " + "?".repeat(65) + Colors.RESET);
            System.out.print(Colors.MAGENTA + "? " + Colors.RESET + Colors.BRIGHT_WHITE + "[" + Colors.RESET);
            int queueCount = 0;
            for (Thread thread : processQueue) {
                Process p = processMap.get(thread);
                if (queueCount > 0) System.out.print(Colors.WHITE + " ? " + Colors.RESET);
                System.out.print(Colors.BRIGHT_CYAN + p.getName() + Colors.RESET);
                queueCount++;
            }
            if (queueCount == 0) {
                System.out.print(Colors.YELLOW + "empty" + Colors.RESET);
            }
            System.out.println(Colors.BRIGHT_WHITE + "]" + Colors.RESET);
            System.out.println(Colors.BOLD + Colors.MAGENTA + "?" + "?".repeat(79) + Colors.RESET + "\n");
            
            currentThread.start();
            
            try {
                currentThread.join();
            } catch (InterruptedException e) {
                System.out.println("Main thread interrupted.");
            }
            
            if (!process.isFinished()) {
                if (!processQueue.isEmpty()) {
                    process.setLastReadyTime(System.currentTimeMillis());
                    addProcessToQueue(process, processQueue, processMap);
                } else {
                    System.out.println(Colors.BRIGHT_YELLOW + "  ? " + Colors.CYAN + process.getName() + 
                                      Colors.RESET + Colors.YELLOW + " is the last process ? running to completion" + 
                                      Colors.RESET);
                    process.runToCompletion(); 
                    completedProcesses.add(process);
                }
            } else {
                completedProcesses.add(process);
            }
        }
        
        System.out.println(Colors.BOLD + Colors.BRIGHT_GREEN + "????????????????????????????????????????????????????????????????????????????????????" + Colors.RESET);
        System.out.println(Colors.BOLD + Colors.BRIGHT_GREEN + "?" + Colors.RESET + Colors.BG_GREEN + Colors.WHITE + Colors.BOLD + "                     ?  ALL PROCESSES COMPLETED  ?                            " + Colors.RESET + Colors.BOLD + Colors.BRIGHT_GREEN + "?" + Colors.RESET);
        System.out.println(Colors.BOLD + Colors.BRIGHT_GREEN + "????????????????????????????????????????????????????????????????????????????????????" + Colors.RESET + "\n");
        
        System.out.println(Colors.BOLD + Colors.BRIGHT_YELLOW + "????????????????????????????????????????????????????????????????????????????????????" + Colors.RESET);
        System.out.println(Colors.BOLD + Colors.BRIGHT_YELLOW + "?" + Colors.RESET + Colors.BG_BLUE + Colors.BRIGHT_WHITE + Colors.BOLD + "                        SCHEDULER STATISTICS                                     " + Colors.RESET + Colors.BOLD + Colors.BRIGHT_YELLOW + "?" + Colors.RESET);
        System.out.println(Colors.BOLD + Colors.BRIGHT_YELLOW + "????????????????????????????????????????????????????????????????????????????????????" + Colors.RESET);
        System.out.println(Colors.BOLD + Colors.BRIGHT_YELLOW + "?" + Colors.RESET + Colors.CYAN + "  ?? Total Context Switches: " + Colors.RESET + Colors.BRIGHT_CYAN + String.format("%-52s", contextSwitchCount) + Colors.BOLD + Colors.BRIGHT_YELLOW + "?" + Colors.RESET);
        System.out.println(Colors.BOLD + Colors.BRIGHT_YELLOW + "????????????????????????????????????????????????????????????????????????????????????" + Colors.RESET + "\n");
        
        displayWaitingTimeSummary();
    }
    
    public static void addProcessToQueue(Process process, Queue<Thread> processQueue, Map<Thread, Process> processMap) {
        Thread thread = new Thread(process);
        processQueue.add(thread);
        processMap.put(thread, process);
        
        System.out.println(Colors.BLUE + "  ? " + Colors.BOLD + Colors.CYAN + process.getName() + 
                          Colors.RESET + Colors.YELLOW + " (Priority: " + process.getPriority() + ")" + 
                          Colors.RESET + Colors.BLUE + " added to ready queue" + Colors.RESET + 
                          " ? Burst time: " + Colors.YELLOW + process.getBurstTime() + "ms" + 
                          Colors.RESET);
    }
    
    public static void displayWaitingTimeSummary() {
        System.out.println(Colors.BOLD + Colors.BRIGHT_CYAN + "????????????????????????????????????????????????????????????????????????????????????" + Colors.RESET);
        System.out.println(Colors.BOLD + Colors.BRIGHT_CYAN + "?" + Colors.RESET + Colors.BG_BLUE + Colors.BRIGHT_WHITE + Colors.BOLD + "                PROCESS WAITING & TURNAROUND TIME SUMMARY                        " + Colors.RESET + Colors.BOLD + Colors.BRIGHT_CYAN + "?" + Colors.RESET);
        System.out.println(Colors.BOLD + Colors.BRIGHT_CYAN + "????????????????????????????????????????????????????????????????????????????????????" + Colors.RESET);
        
        System.out.println(Colors.BOLD + Colors.BRIGHT_CYAN + "?" + Colors.RESET + "  " + Colors.BOLD + Colors.BRIGHT_WHITE + 
                          String.format("%-10s", "Process") + String.format("%-12s", "Burst Time") + 
                          String.format("%-10s", "Priority") + String.format("%-15s", "Waiting Time") + 
                          String.format("%-18s", "Turnaround Time") + Colors.RESET + "   " + Colors.BOLD + Colors.BRIGHT_CYAN + "?" + Colors.RESET);
        
        System.out.println(Colors.BOLD + Colors.BRIGHT_CYAN + "????????????????????????????????????????????????????????????????????????????????????" + Colors.RESET);
        
        long totalWaitingTime = 0;
        long totalTurnaroundTime = 0;
        
        for (Process process : completedProcesses) {
            long turnaroundTime = process.getTurnaroundTime();
            String waitTimeStr = process.getTotalWaitingTime() + "ms";
            String turnaroundStr = turnaroundTime + "ms";
            
            System.out.println(Colors.BOLD + Colors.BRIGHT_CYAN + "?" + Colors.RESET + "  " + Colors.BRIGHT_CYAN + 
                              String.format("%-10s", process.getName()) + Colors.RESET + Colors.YELLOW + 
                              String.format("%-12s", process.getBurstTime() + "ms") + Colors.RESET + Colors.MAGENTA + 
                              String.format("%-10s", process.getPriority()) + Colors.RESET + Colors.BRIGHT_GREEN + 
                              String.format("%-15s", waitTimeStr) + Colors.RESET + Colors.BRIGHT_YELLOW + 
                              String.format("%-18s", turnaroundStr) + Colors.RESET + "   " + Colors.BOLD + Colors.BRIGHT_CYAN + "?" + Colors.RESET);
            
            totalWaitingTime += process.getTotalWaitingTime();
            totalTurnaroundTime += turnaroundTime;
        }
        
        System.out.println(Colors.BOLD + Colors.BRIGHT_CYAN + "????????????????????????????????????????????????????????????????????????????????????" + Colors.RESET);
        
        double avgWaitingTime = (double) totalWaitingTime / completedProcesses.size();
        double avgTurnaroundTime = (double) totalTurnaroundTime / completedProcesses.size();
        
        System.out.println(Colors.BOLD + Colors.BRIGHT_CYAN + "?" + Colors.RESET + "  " + Colors.BOLD + Colors.BRIGHT_YELLOW + 
                          String.format("%-47s", "Averages:") + 
                          String.format("%-15s", String.format("%.2fms", avgWaitingTime)) + 
                          String.format("%-18s", String.format("%.2fms", avgTurnaroundTime)) + 
                          Colors.RESET + "   " + Colors.BOLD + Colors.BRIGHT_CYAN + "?" + Colors.RESET);
        System.out.println(Colors.BOLD + Colors.BRIGHT_CYAN + "????????????????????????????????????????????????????????????????????????????????????" + Colors.RESET + "\n");
    }
}
