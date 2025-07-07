package org.example.core.config;

import org.example.core.dto.CreateProfessionalRequest;
import org.example.core.dto.CreateUserRequest;
import org.example.core.model.Permission;
import org.example.core.model.Role;
import org.example.core.model.enums.UserType;
import org.example.core.repository.MenuRepository;
import org.example.core.repository.PermissionRepository;
import org.example.core.repository.RoleRepository;
import org.example.core.repository.UserRepository;
import org.example.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;
    private final MenuRepository menuRepository;
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Autowired
    public DataInitializer(UserService userService, MenuRepository menuRepository, PermissionRepository permissionRepository, RoleRepository roleRepository, UserRepository userRepository) {
        this.userService = userService;
        this.menuRepository = menuRepository;
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        // 1. ایجاد نقش‌ها و مجوزها اگر وجود ندارند
        if (roleRepository.count() == 0) {
            createPermissionsAndRoles();
        }

        // 2. ایجاد کاربر ادمین پیش‌فرض اگر هیچ کاربری وجود ندارد
        userService.createDefaultAdminIfNotExists();

        // 3. ایجاد منوهای اولیه اگر وجود ندارند
        if (menuRepository.count() == 0) {
            createMenus();
        }

        // 4. ایجاد کاربران نمونه برای تست
        createSampleUsersIfNotFound();
    }

    private void createPermissionsAndRoles() {
        System.out.println("✅ Creating initial permissions and roles...");

        // تعریف و ذخیره مجوزها
        Permission permViewDashboard = createAndSavePermission("VIEW_DASHBOARD");
        Permission permManageUsers = createAndSavePermission("MANAGE_USERS");
        Permission permViewReports = createAndSavePermission("VIEW_REPORTS");

        // تعریف و ذخیره نقش‌ها با مجوزهای مربوطه
        createAndSaveRole("ROLE_ADMIN", permViewDashboard, permManageUsers, permViewReports);
        createAndSaveRole("ROLE_PROFESSIONAL", permViewDashboard);
        createAndSaveRole("ROLE_CLIENT", permViewDashboard);

        System.out.println("✅ Initial permissions and roles created.");
    }

    // متد کمکی برای ایجاد و ذخیره یک مجوز
    private Permission createAndSavePermission(String name) {
        Permission permission = new Permission();
        permission.setName(name);
        return permissionRepository.save(permission);
    }

    // متد کمکی برای ایجاد و ذخیره یک نقش
    private void createAndSaveRole(String name, Permission... permissions) {
        Role role = new Role();
        role.setName(name);
        role.setPermissions(new HashSet<>(List.of(permissions)));
        roleRepository.save(role);
    }

    private void createMenus() {
        // منطق ایجاد منوهای شما در اینجا قرار می‌گیرد
        System.out.println("✅ Creating initial menu items...");
        // ... (your menu creation logic)
    }

    private void createSampleUsersIfNotFound() {
        if (userRepository.count() < 5) {
            System.out.println("✅ Creating sample users...");

            List<CreateUserRequest> clients = List.of(
                    buildClient("مریم", "حسینی", "maryam.hosseini@example.com", "09127778899"),
                    buildClient("علی", "کریمی", "ali.karimi@example.com", "09123334455"),
                    buildClient("نازنین", "محمدی", "nazanin.mohammadi@example.com", "09126667788")
            );

            // 2. Define Sample Professionals
            List<CreateProfessionalRequest> professionals = List.of(
                    // 1. Legal Professional
                    buildProfessional(
                            "Sarah", "Miller", "sarah.miller@example.com", "555-123-4567",
                            "Experienced family lawyer with 10 years in divorce and custody cases.", "Family Law", 60, new BigDecimal("150.00"),
                            "https://example.com/sarah_profile.jpg"
                    ),
                    // 2. Financial Professional
                    buildProfessional(
                            "John", "Smith", "john.smith@example.com", "555-234-5678",
                            "Financial and investment advisor specializing in stock market and crypto.", "Financial Consulting", 45, new BigDecimal("200.00"),
                            "https://example.com/john_profile.jpg"
                    ),
                    // 3. Child Psychologist
                    buildProfessional(
                            "Emily", "White", "emily.white@example.com", "555-345-6789",
                            "Child and adolescent psychologist, expert in behavioral and learning disorders.", "Child Psychology", 90, new BigDecimal("180.00"),
                            "https://example.com/emily_profile.jpg"
                    ),
                    // 4. Dermatology Specialist
                    buildProfessional(
                            "Robert", "Jones", "robert.jones@example.com", "555-456-7890",
                            "Dermatologist offering aesthetic and medical skin and hair treatments.", "Dermatology", 30, new BigDecimal("300.00"),
                            "https://example.com/robert_profile.jpg"
                    ),
                    // 5. Math Tutor
                    buildProfessional(
                            "Jessica", "Brown", "jessica.brown@example.com", "555-567-8901",
                            "Private math tutor for high school and university students.", "Mathematics Tutoring", 60, new BigDecimal("80.00"),
                            "https://example.com/jessica_profile.jpg"
                    ),
                    // 6. IT & Network Specialist
                    buildProfessional(
                            "David", "Garcia", "david.garcia@example.com", "555-678-9012",
                            "IT expert specializing in network security and programming.", "IT & Networking", 75, new BigDecimal("220.00"),
                            "https://example.com/david_profile.jpg"
                    ),
                    // 7. Nutritionist
                    buildProfessional(
                            "Sophia", "Rodriguez", "sophia.rodriguez@example.com", "555-789-0123",
                            "Nutritionist and dietitian, assisting with weight loss and general health.", "Nutrition & Dietetics", 45, new BigDecimal("120.00"),
                            "https://example.com/sophia_profile.jpg"
                    ),
                    // 8. Architect & Designer
                    buildProfessional(
                            "Daniel", "Martinez", "daniel.martinez@example.com", "555-890-1234",
                            "Architect and interior designer, experienced in residential and commercial spaces.", "Architecture & Design", 90, new BigDecimal("400.00"),
                            "https://example.com/daniel_profile.jpg"
                    ),
                    // 9. Academic Advisor
                    buildProfessional(
                            "Olivia", "Hernandez", "olivia.hernandez@example.com", "555-901-2345",
                            "University entrance exam consultant and study planner, specialized in science fields.", "Academic Advising", 60, new BigDecimal("110.00"),
                            "https://example.com/olivia_profile.jpg"
                    ),
                    // 10. English Language Instructor
                    buildProfessional(
                            "Michael", "Lopez", "michael.lopez@example.com", "555-012-3456",
                            "English language instructor focusing on IELTS and TOEFL preparation.", "English Language Teaching", 60, new BigDecimal("95.00"),
                            "https://example.com/michael_profile.jpg"
                    ),
                    // 11. Sports Physiotherapist
                    buildProfessional(
                            "Ava", "Gonzales", "ava.gonzales@example.com", "555-112-2334",
                            "Sports physiotherapist specializing in athletic injury rehabilitation.", "Physiotherapy", 30, new BigDecimal("175.00"),
                            "https://example.com/ava_profile.jpg"
                    ),
                    // 12. Insurance Consultant
                    buildProfessional(
                            "William", "Wilson", "william.wilson@example.com", "555-223-3445",
                            "Insurance expert specializing in life and third-party liability insurance.", "Insurance Consulting", 45, new BigDecimal("70.00"),
                            "https://example.com/william_profile.jpg"
                    ),
                    // 13. Graphic & Web Designer
                    buildProfessional(
                            "Mia", "Anderson", "mia.anderson@example.com", "555-334-4556",
                            "Graphic and web designer, expert in UI/UX and branding.", "Graphic & Web Design", 60, new BigDecimal("240.00"),
                            "https://example.com/mia_profile.jpg"
                    ),
                    // 14. Criminal & Commercial Lawyer
                    buildProfessional(
                            "James", "Thomas", "james.thomas@example.com", "555-445-5667",
                            "Legal counsel with experience in criminal and commercial litigation.", "Criminal & Commercial Law", 60, new BigDecimal("210.00"),
                            "https://example.com/james_profile.jpg"
                    ),
                    // 15. SEO & Digital Marketing Specialist
                    buildProfessional(
                            "Charlotte", "Jackson", "charlotte.jackson@example.com", "555-556-6778",
                            "SEO and digital marketing specialist, helping improve website rankings.", "Digital Marketing", 45, new BigDecimal("290.00"),
                            "https://example.com/charlotte_profile.jpg"
                    )
            );

            clients.forEach(request -> {
                if (userRepository.findByUsername(request.getUsername()).isEmpty()) {
                    try {
                        // این متد باید در UserService اصلاح شود
                        userService.createUser(request);
                        System.out.println(" -> Created " + request.getUserType() + ": " + request.getUsername());
                    } catch (Exception e) {
                        System.err.println("❌ Error creating sample user '" + request.getUsername() + "': " + e.getMessage());
                        e.printStackTrace(); // چاپ کامل خطا برای دیباگ بهتر
                    }
                }
            });

            professionals.forEach(request -> {
                if (userRepository.findByUsername(request.getUsername()).isEmpty()) {
                    try {
                        // این متد باید در UserService اصلاح شود
                        userService.createUser(request);
                        System.out.println(" -> Created " + request.getUserType() + ": " + request.getUsername());
                    } catch (Exception e) {
                        System.err.println("❌ Error creating sample user '" + request.getUsername() + "': " + e.getMessage());
                        e.printStackTrace(); // چاپ کامل خطا برای دیباگ بهتر
                    }
                }
            });
        }
    }

    private CreateUserRequest buildClient(String firstName, String lastName, String email, String phone) {
        CreateUserRequest request = new CreateUserRequest();
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setUsername(email); // Using email as username is common practice
        request.setPassword("securePassword123"); // Use a strong default password
        request.setPhoneNumber(phone);
        request.setUserType(UserType.CLIENT);
        request.setRoles(List.of("ROLE_CLIENT")); // Use Set.of for immutable sets

        return request;
    }

    private CreateProfessionalRequest buildProfessional(
            String firstName, String lastName, String email, String phone,
            String bio, String specialty, Integer consultationDurationMinutes,
            BigDecimal consultationPrice, String profilePictureUrl) {

        CreateProfessionalRequest request = new CreateProfessionalRequest();
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setUsername(email); // Using email as username
        request.setPassword("securePassword123"); // Strong default password
        request.setPhoneNumber(phone);
        request.setUserType(UserType.PROFESSIONAL);
        request.setRoles(List.of("ROLE_PROFESSIONAL")); // Use Set.of for immutable sets

        // Professional-specific fields
        request.setBio(bio);
        request.setSpecialty(specialty);
        request.setConsultationDurationMinutes(consultationDurationMinutes);
        request.setConsultationPrice(consultationPrice);
        request.setProfilePictureUrl(profilePictureUrl);

        return request;
    }


}
