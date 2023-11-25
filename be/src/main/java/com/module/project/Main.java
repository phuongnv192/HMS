package com.module.project;

import com.module.project.dto.ConfirmStatus;
import com.module.project.dto.Constant;
import com.module.project.dto.RoleEnum;
import com.module.project.dto.TransactionStatus;
import com.module.project.model.Booking;
import com.module.project.model.BookingSchedule;
import com.module.project.model.BookingTransaction;
import com.module.project.model.Branch;
import com.module.project.model.Cleaner;
import com.module.project.model.Role;
import com.module.project.model.Service;
import com.module.project.model.ServiceAddOn;
import com.module.project.model.ServicePackage;
import com.module.project.model.ServiceType;
import com.module.project.model.User;
import com.module.project.repository.BookingRepository;
import com.module.project.repository.BookingScheduleRepository;
import com.module.project.repository.BookingTransactionRepository;
import com.module.project.repository.BranchRepository;
import com.module.project.repository.CleanerRepository;
import com.module.project.repository.RoleRepository;
import com.module.project.repository.ServiceAddOnRepository;
import com.module.project.repository.ServicePackageRepository;
import com.module.project.repository.ServiceRepository;
import com.module.project.repository.ServiceTypeRepository;
import com.module.project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.ZoneId;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class Main implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ServiceTypeRepository serviceTypeRepository;

    @Autowired
    private ServicePackageRepository servicePackageRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ServiceAddOnRepository serviceAddOnRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingTransactionRepository bookingTransactionRepository;

    @Autowired
    private BookingScheduleRepository bookingScheduleRepository;

    @Autowired
    private CleanerRepository cleanerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        try {
            Role[] roles = new Role[]{
                    new Role(1L, RoleEnum.ADMIN.name()),
                    new Role(2L, RoleEnum.MANAGER.name()),
                    new Role(3L, RoleEnum.LEADER.name()),
                    new Role(4L, RoleEnum.CLEANER.name()),
                    new Role(5L, RoleEnum.CUSTOMER.name()),
            };
            roleRepository.saveAll(Arrays.asList(roles));

            Branch branch = Branch.builder()
                    .branchName("Ha Noi")
                    .status(Constant.COMMON_STATUS.ACTIVE)
                    .build();
            branchRepository.save(branch);

            User user1 = User.builder()
                    .firstName("Duong")
                    .lastName("PL")
                    .username("manager")
                    .password(passwordEncoder.encode("1"))
                    .status(Constant.COMMON_STATUS.ACTIVE)
                    .role(roles[1])
                    .build();
            User user2 = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("1"))
                    .status(Constant.COMMON_STATUS.ACTIVE)
                    .role(roles[0])
                    .build();
            User user3 = User.builder()
                    .username("cleaner")
                    .password(passwordEncoder.encode("1"))
                    .status(Constant.COMMON_STATUS.ACTIVE)
                    .role(roles[3])
                    .build();
            User user4 = User.builder()
                    .username("leader")
                    .password(passwordEncoder.encode("1"))
                    .status(Constant.COMMON_STATUS.ACTIVE)
                    .role(roles[2])
                    .build();
            userRepository.saveAll(Arrays.asList(user1, user2, user3, user4));

//            String review = "{\n" +
//                    "    \"1\": {\n" +
//                    "        \"cleanerActivities\": [\n" +
//                    "            {\n" +
//                    "                \"bookingScheduleId\": 1,\n" +
//                    "                \"workDate\": 1699882588882,\n" +
//                    "                \"ratingScore\": 5,\n" +
//                    "                \"review\": \"good\"\n" +
//                    "            },\n" +
//                    "            {\n" +
//                    "                \"bookingScheduleId\": 1,\n" +
//                    "                \"workDate\": 1699882588882,\n" +
//                    "                \"ratingScore\": 5,\n" +
//                    "                \"review\": \"good\"\n" +
//                    "            }\n" +
//                    "        ]\n" +
//                    "    },\n" +
//                    "    \"2\": {\n" +
//                    "        \"cleanerActivities\": [\n" +
//                    "            {\n" +
//                    "                \"bookingScheduleId\": 1,\n" +
//                    "                \"workDate\": 1699882588882,\n" +
//                    "                \"ratingScore\": 5,\n" +
//                    "                \"review\": \"good\"\n" +
//                    "            },\n" +
//                    "            {\n" +
//                    "                \"bookingScheduleId\": 1,\n" +
//                    "                \"workDate\": 1699882588882,\n" +
//                    "                \"ratingScore\": 5,\n" +
//                    "                \"review\": \"good\"\n" +
//                    "            }\n" +
//                    "        ]\n" +
//                    "    }\n" +
//                    "}";
            Cleaner cleaner1 = Cleaner.builder()
                    .user(user1)
                    .branch(branch)
                    .status(Constant.COMMON_STATUS.ACTIVE)
                    .idCard("1")
//                    .review(review)
                    .build();
            Cleaner cleaner2 = Cleaner.builder()
                    .user(user2)
                    .branch(branch)
                    .status(Constant.COMMON_STATUS.ACTIVE)
                    .idCard("2")
//                    .review(review)
                    .build();
            Cleaner cleaner3 = Cleaner.builder()
                    .user(user3)
                    .branch(branch)
                    .status(Constant.COMMON_STATUS.ACTIVE)
                    .idCard("2")
//                    .review(review)
                    .build();
            Cleaner cleaner4 = Cleaner.builder()
                    .user(user4)
                    .branch(branch)
                    .status(Constant.COMMON_STATUS.ACTIVE)
                    .idCard("2")
//                    .review(review)
                    .build();
            cleanerRepository.saveAll(Arrays.asList(cleaner1, cleaner2, cleaner3, cleaner4));

            ServiceType serviceType = ServiceType.builder()
                    .serviceTypeName("Daily on the basis")
                    .build();
            ServiceType serviceType1 = ServiceType.builder()
                    .serviceTypeName("Weekly on the basis")
                    .build();
            ServiceType serviceType2 = ServiceType.builder()
                    .serviceTypeName("Monthly on the basis")
                    .build();
            List<ServiceType> serviceTypeList = Arrays.asList(serviceType, serviceType1, serviceType2);
            serviceTypeRepository.saveAll(serviceTypeList);

            ServicePackage servicePackage = ServicePackage.builder()
                    .servicePackageName("1")
                    .serviceType(serviceType)
                    .build();
            ServicePackage servicePackage1 = ServicePackage.builder()
                    .servicePackageName("3")
                    .serviceType(serviceType1)
                    .build();
            ServicePackage servicePackage2 = ServicePackage.builder()
                    .servicePackageName("6")
                    .serviceType(serviceType2)
                    .build();
            ServicePackage servicePackage3 = ServicePackage.builder()
                    .servicePackageName("12")
                    .serviceType(serviceType2)
                    .build();
            List<ServicePackage> servicePackages = Arrays.asList(servicePackage, servicePackage1, servicePackage2, servicePackage3);
            servicePackageRepository.saveAll(servicePackages);

            Service service = Service.builder()
                    .serviceName("Apartment cleaning")
                    .description("Apartment cleaning")
                    .status(Constant.COMMON_STATUS.ACTIVE)
                    .paymentMethod(Constant.PAYMENT_TYPE.CASH)
                    .serviceType(serviceType)
                    .build();
            serviceRepository.save(service);

            ServiceAddOn serviceAddOn = ServiceAddOn.builder()
                    .name("Washing machine service")
                    .status(Constant.COMMON_STATUS.ACTIVE)
                    .service(service)
                    .build();
            serviceAddOnRepository.save(serviceAddOn);
            ServiceAddOn serviceAddOn1 = ServiceAddOn.builder()
                    .name("Vệ sinh máy giặt không tháo lồng")
                    .parentId(serviceAddOn.getId())
                    .price(10000)
                    .status(Constant.COMMON_STATUS.ACTIVE)
                    .build();
            ServiceAddOn serviceAddOn2 = ServiceAddOn.builder()
                    .name("Vệ sinh và bảo trì máy giặt lồng")
                    .parentId(serviceAddOn.getId())
                    .price(20000)
                    .status(Constant.COMMON_STATUS.ACTIVE)
                    .build();
            serviceAddOnRepository.saveAll(Arrays.asList(serviceAddOn1, serviceAddOn2));

//            Booking booking = Booking.builder()
//                    .hostName("Booking guest name")
//                    .houseType(Constant.HOUSE_TYPE.APARTMENT)
//                    .floorNumber(12)
//                    .floorArea(120f)
//                    .status(ConfirmStatus.RECEIVED.name())
//                    .cleaners(Set.of(cleaner1, cleaner2))
//                    .user(user1)
//                    .userUpdate(user1)
//                    .build();
//            bookingRepository.save(booking);
//
//            BookingTransaction bookingTransaction = BookingTransaction.builder()
//                    .booking(booking)
//                    .totalBookingCleaner(2)
//                    .totalBookingPrice(1000000D)
//                    .totalBookingDate(12)
//                    .status(ConfirmStatus.RECEIVED.name())
//                    .servicePackage(servicePackage3)
//                    .build();
//            bookingTransactionRepository.save(bookingTransaction);
//
//            Calendar calendar = Calendar.getInstance();
//            BookingSchedule bookingSchedule = BookingSchedule.builder()
//                    .status(TransactionStatus.MATCHED.name())
//                    .bookingTransaction(bookingTransaction)
//                    .serviceAddOns(Set.of(serviceAddOn1))
//                    .workDate(calendar.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
//                    .build();
//
//            calendar.add(Calendar.DATE, 1);
//            BookingSchedule bookingSchedule1 = BookingSchedule.builder()
//                    .status(TransactionStatus.MATCHED.name())
//                    .bookingTransaction(bookingTransaction)
//                    .serviceAddOns(Set.of(serviceAddOn1))
//                    .workDate(calendar.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
//                    .build();
//            bookingScheduleRepository.saveAll(Set.of(bookingSchedule, bookingSchedule1));

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

}
