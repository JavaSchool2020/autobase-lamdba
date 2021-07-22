package com.akalinkina;

import com.akalinkina.model.Car;
import com.akalinkina.model.Owner;
import com.akalinkina.storage.CarStorage;

import java.util.*;

public class Main {
    public final CarStorage carStorage = CarStorage.getStorage();
    private Scanner scanner;

    public static void main(String[] args) {
        new Main().start();
    }

    private void start() {
        this.scanner = new Scanner(System.in);
        try {
            int command;
            do {
                command = mainMenu();
                switch (command) {
                    case 1: add(); break;
                    case 2: remove(); break;
                    case 3: updateCarOwner(); break;
                    case 4: findCars(); break;
                    case 5: findOwners(); break;
                    default: break;
                }
            } while (command < 6);
        } catch (Exception e) {
            System.out.print("Произошла непредвиденная ошибка: " + e.getMessage() );
        } finally {
            if (this.scanner != null) {
                this.scanner.close();
            }
        }
    }

    private int mainMenu() {
        return readInt("\nВыберите команду: \n1. Добавить автомобиль\n2. Удалить автомобиль\n3. Добавить/изменить владельца\n4. Найти транспорт" +
                        "\n5. Найти владельца\n6. Выйти",
                6);
    }

    private int finderCarMenu() {
        return readInt("\nНайти: \n1. Все автомобили\n2. Автомобиль по vin\n3. Автомобили по ФИО владельца" +
                        "\n4. Автомобили по ид владельца\n5. Вернуться в главное меню",
                5);
    }

    private int finderOwnerMenu() {
        return readInt("\nНайти: \n1. Всех владельцев автомобилей\n2. Все владельцев и их автомобили\n3. Вернуться в главное меню",
                3);
    }

    private void add() {
        String continueMessage = "Добавить автомобиль с другим vin";
        do {
            String vin = readVin();
            try {
                Car car = carStorage.addCar(vin);
                System.out.println("\nАвтомобиль успешно добавлен: vin = " + car.getVin());
                break;
            } catch (IllegalArgumentException illegalArgumentException) {
                System.out.println("\n" + illegalArgumentException.getMessage());
            }
        } while (isContinue(continueMessage));

    }

    private  void remove() {
        String continueMessage = "Удалить автомобиль с другим vin";
        do {
            String vin = readVin();
            if (carStorage.delete(vin)) {
                System.out.println("Автомобиль успешно удален: vin = " + vin);
                break;
            }
            System.out.println("\nАвтомобиль с таким vin не найден");
        } while (isContinue(continueMessage));
    }

    private  void updateCarOwner() {
        String continueMessage = "Изменить владельца автомобиля с другими данными";
        do {
            String vin = readVin();
            String fullName = readFullName();
            long id = readOwnerId();
            try {
                Owner owner = new Owner(fullName, id);
                Car car = carStorage.updateOwner(vin, owner);
                System.out.println("Владелец автомобиля успешно изменен: vin = " + car.getVin() + ", вдаделец "
                        + car.getOwner().getFullName() + " " + car.getOwner().getId());
                break;
            } catch (IllegalArgumentException illegalArgumentException) {
                System.out.println("\n" + illegalArgumentException.getMessage());
            }
        } while (isContinue(continueMessage));
    }

    private void findCars() {
        int findType = finderCarMenu();
        Set<Car> carSet;
        switch (findType) {
            case 1: carSet = carStorage.getAll(); break;
            case 2: {
                carSet = carStorage.getByVin(readVin()).map(Collections::singleton).orElse(Collections.emptySet());
                break;
            }
            case 3: carSet = carStorage.getByFullName(readFullName()); break;
            case 4: carSet = carStorage.getById(readOwnerId()); break;
            default: return;
        }

        if (carSet == null || carSet.isEmpty()) {
            System.out.println("По заданным параметром автомобиль не найден");
        } else {
            System.out.println("Найденные автомобили:");
            for (Car car: carSet) {
                Owner owner = car.getOwner();
                System.out.println("vin = " + car.getVin() + (owner != null ? (", владелец " + owner.getFullName()) : ""));
            }
        }
    }

    private void findOwners() {
        int findType = finderOwnerMenu();
        switch (findType) {
            case 1: showOwners(); break;
            case 2: showOwnersWithCars(); break;
            default: break;
        }
    }

    private void showOwners() {
        Collection<Owner> owners = carStorage.getOwners();

        if (owners == null || owners.isEmpty()) {
            System.out.println("По заданным параметром владелец не найден");
        } else {
            System.out.println("Результат поиска:");
            for (Owner owner: owners) {
                System.out.println("Владелец = " + owner.getFullName());
            }
        }

    }

    private void showOwnersWithCars() {
        Map<Owner, Set<String>> ownerSetMap = carStorage.mapByOwner();

        if (ownerSetMap == null || ownerSetMap.isEmpty()) {
            System.out.println("По заданным параметром владелец не найден");
        } else {
            System.out.println("Результат поиска:");
            for (Map.Entry<Owner, Set<String>> entry: ownerSetMap.entrySet()) {
                System.out.println("Владелец = " + entry.getKey().getFullName() + ", список автомобилей : " + entry.getValue());
            }
        }
    }

    private boolean isContinue(String firstPoint) {
        int command = readInt("1. " + firstPoint + "\n2. Вернуться в главное меню",
                2);
        return command == 1 ? Boolean.TRUE : Boolean.FALSE;
    }

    private String readVin() {
        return readString("\nВведите VIN транспорта");
    }

    private String readFullName() {
        return readString("\nВведите ФИО владельца");
    }

    private long readOwnerId() {
        return readLong("\nВведите ид паспорта", 9999999999L);
    }

    private String readString(String text) {
        String vin = "";
        do {
            System.out.println(text);
            vin = this.scanner.nextLine();
        } while (vin.isEmpty());

        return vin;
    }

    private long readLong(String text, long maxValue) {
        long value = 0L;
        do {
            System.out.println(text);
            try {
                value = Long.parseLong(scanner.nextLine());
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Ожидается вввод цифры");
            }
        } while (value <=0 || value > maxValue);

        return value;
    }

    private int readInt(String text, int maxValue) {
        int command = 0;
        do {
            System.out.println(text);
            try {
                String value = scanner.nextLine();
                command = Integer.parseInt(value);
            } catch (Exception e) {
                System.out.println("Ожидается вввод цифры");
            }
        } while (command <=0 || command > maxValue);

        return command;
    }
}
