import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class MainApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(MainApplication.class, args);

        // โหลดข้อมูลจากไฟล์ JSON
        ClassPathResource resource = new ClassPathResource("data.json");
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = objectMapper.readValue(resource.getInputStream(), Map.class);

        // เก็บข้อมูลสรุปตามแผนก
        Map<String, Map<String, Object>> departmentSummary = new HashMap<>();

        // วนลูปผ่านข้อมูล users
        for (Map<String, Object> user : (Iterable<Map<String, Object>>) data.get("users")) {
            // ดึงข้อมูลแผนก
            String department = (String) ((Map<String, Object>) user.get("company")).get("department");

            // กำหนด key ให้แผนก (หากยังไม่มี)
            departmentSummary.putIfAbsent(department, new HashMap<>());

            // นับจำนวนเพศ
            String gender = (String) user.get("gender");
            departmentSummary.get(department).put(gender, departmentSummary.get(department).getOrDefault(gender, 0) + 1);

            // ประมวลผลอายุ
            int age = (int) user.get("age");
            String ageRange = departmentSummary.get(department).containsKey("ageRange") ?
                    (String) departmentSummary.get(department).get("ageRange") : "";
            if (ageRange.isEmpty()) {
                ageRange = age + "-" + age;
            } else {
                String[] ages = ageRange.split("-");
                int minAge = Math.min(Integer.parseInt(ages[0]), age);
                int maxAge = Math.max(Integer.parseInt(ages[1]), age);
                ageRange = minAge + "-" + maxAge;
            }
            departmentSummary.get(department).put("ageRange", ageRange);

            // นับจำนวนสีผม
            String hairColor = ((Map<String, String>) user.get("hair")).get("color");
            departmentSummary.get(department).put(hairColor, departmentSummary.get(department).getOrDefault(hairColor, 0) + 1);

            // เก็บข้อมูลที่อยู่
            String fullName = (String) user.get("firstName") + (String) user.get("lastName");
            String postalCode = (String) ((Map<String, Object>) user.get("address")).get("postalCode");
            departmentSummary.get(department).put(fullName, postalCode);
        }

        // แสดงผลลัพธ์ในรูปแบบที่ต้องการ
        for (String department : departmentSummary.keySet()) {
            System.out.println(department + ": ");
            System.out.println("    male : " + departmentSummary.get(department).getOrDefault("male", 0));
            System.out.println("    female : " + departmentSummary.get(department).getOrDefault("female", 0));
            System.out.println("    ageRange: " + departmentSummary.get(department).get("ageRange"));
            System.out.println("    hair : ");
            departmentSummary.get(department).entrySet().stream()
                    .filter(entry -> entry.getKey().matches("Black|Blond|Chestnut|Brown"))
                    .forEach(entry -> System.out.println("        " + entry.getKey() + ": " + entry.getValue()));
            System.out.println("    addressUser : ");
            departmentSummary.get(department).entrySet().stream()
                    .filter(entry -> !entry.getKey().equals("male") && !entry.getKey().equals("female") && !entry.getKey().equals("ageRange"))
                    .forEach(entry -> System.out.println("        " + entry.getKey() + ": " + entry.getValue()));
        }
    }
}
