package com.company;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

public class Database {
    static HashMap<String, Tenant> tenantMap = new HashMap<>();
    static HashMap<String, Student> applicationMap = new HashMap<>();
    static HashMap<String, Lease> leaseMap = new HashMap<>();
    static HashMap<Integer, Room> roomMap = new HashMap<>();
    static HashMap<String, Hall> hallMap = new HashMap<>();
    static HashMap<String, Login> loginMap = new HashMap<>();

    protected static void getData() {
        try {
            Scanner tenSc = new Scanner(new File("resources/students.csv"));
            Scanner tenSc2 = new Scanner(new File("resources/students.csv"));
            Scanner appSc = new Scanner(new File("resources/applications.csv"));
            Scanner leaseSc = new Scanner(new File("resources/leases.csv"));
            Scanner roomSc = new Scanner(new File("resources/rooms.csv"));
            Scanner roomSc2 = new Scanner(new File("resources/rooms.csv"));
            Scanner hallSc = new Scanner(new File("resources/halls.csv"));
            Scanner loginSc = new Scanner(new File("resources/logins.csv"));

            // skip headers
            Scanner[] scanners = {tenSc, tenSc2, appSc, leaseSc, roomSc, roomSc2, hallSc, loginSc};
            Arrays.stream(scanners).forEach(Scanner::nextLine);

            // APPLICATIONS
            while (appSc.hasNextLine()) {
                String[] arr = appSc.nextLine().split(",");
                Student student = new Student(arr[0], arr[1], arr[2]);
                applicationMap.put(arr[2], student);
            }

            // STUDENTS
            while (tenSc.hasNextLine()) {
                String[] arr = tenSc.nextLine().split(",");
                Tenant tenant = new Tenant(arr[0], arr[1], arr[2]);
                tenantMap.put(arr[2], tenant);
            }

            // ROOMS, only add room number first, because Lease obj and Hall obj have not been created
            while(roomSc.hasNextLine()) {
                String[] arr = roomSc.nextLine().split(",");
                Room room = new Room(Integer.parseInt(arr[0]));
                roomMap.put(room.getNumber(), room);
            }

            // LEASES
            while(leaseSc.hasNextLine()) {
                String[] arr = leaseSc.nextLine().split(",");
                Lease lease = new Lease(arr[0], roomMap.get(Integer.parseInt(arr[2])), tenantMap.get(arr[3]));
                leaseMap.put(lease.getLeaseNo(), lease);
            }

            while (tenSc2.hasNextLine()) {
                String[] arr = tenSc2.nextLine().split(",");
                Tenant currentTenant = tenantMap.get(arr[2]);
                currentTenant.setLease(leaseMap.get(arr[3]));
            }

            // HALLS
            while (hallSc.hasNextLine()) {
                String[] arr = hallSc.nextLine().split(",(?!\\s)");    // match all commas only if not followed by a space (\s).

                // h[0]: name, h[1]: number, h[2]: hallAddress, h[3]: tel, h[4]: totalRooms, h[5]: rooms
                ArrayList<Room> roomsInHall = new ArrayList<>();
                // array containing room numbers, used to add Room into Hall
                int[] roomsNo = Arrays.stream(arr[5].split(" ")).mapToInt(Integer::parseInt).toArray();
                for(int num : roomsNo) {
                    roomsInHall.add(roomMap.get(num));
                }

                Hall hall = new Hall(arr[0],arr[1], arr[2].replaceAll("\"", ""), arr[3], Integer.parseInt(arr[4]), roomsInHall);
                hallMap.put(hall.getID(), hall);
            }

            // ADD REMAINING INFO INTO ROOMS
            while(roomSc2.hasNextLine()) {
                String[] arr = roomSc2.nextLine().split(",");
                // [0]: number, [1]: monthlyRent, [2]: hall (no), [3]: lease (no), [4]: getIsOccupied, [5]: cleaningStatus, [6]: availability
                Room currentRoom = roomMap.get(Integer.parseInt(arr[0]));
                currentRoom.setMonthlyRent(Integer.parseInt(arr[1]));
                currentRoom.setHall(hallMap.get(arr[2]));
                if (!arr[3].equals("null"))
                    currentRoom.setLease(leaseMap.get(arr[3]));
                currentRoom.setOccupied(arr[4].equals("Yes"));
                currentRoom.setCleaningStatus(arr[5]);
                currentRoom.setAvailable(arr[6].equals("Yes"));
            }

            // LOGIN INFO
            while(loginSc.hasNextLine()) {
                String[] arr = loginSc.nextLine().split(",");
                // [0]: email, [1]: password, [2]: staff, [3]: hall
                Login login = new Login(arr[0], arr[1], arr[2], hallMap.get(arr[3]));
                loginMap.put(arr[0], login);

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
