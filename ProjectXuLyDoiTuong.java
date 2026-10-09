import UDP.Student;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ProjectXuLyDoiTuong {
    public static void main(String[] args) {
        String studentCode = "B23DCDT169";
        String qCode = "5CCWNp5n";
        String serverIp = "36.50.135.242";
        int port = 2209;

        DatagramSocket socket = null;

        try {
            socket = new DatagramSocket();
            InetAddress serverAddress = InetAddress.getByName(serverIp);
            socket.setSoTimeout(5000);

            String requestMessage = ";" + studentCode + ";" + qCode;
            byte[] sendData1 = requestMessage.getBytes();
            DatagramPacket sendPacket1 = new DatagramPacket(sendData1, sendData1.length, serverAddress, port);
            socket.send(sendPacket1);
            System.out.println("Đã gửi request: " + requestMessage);

            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            socket.receive(receivePacket);

            String requestId = new String(receivePacket.getData(), 0, 8);
            System.out.println("Đã nhận requestId: " + requestId);

            ByteArrayInputStream bais = new ByteArrayInputStream(receivePacket.getData(), 8, receivePacket.getLength() - 8);
            ObjectInputStream ois = new ObjectInputStream(bais);
            Student student = (Student) ois.readObject();
            System.out.println("Tên gốc nhận được: " + student.name);

            String[] words = student.name.trim().toLowerCase().split("\\s+");

            StringBuilder normalizedName = new StringBuilder();
            StringBuilder emailPrefix = new StringBuilder(words[words.length - 1]);

            for (int i = 0; i < words.length; i++) {
                String word = words[i];
                if (word.length() > 0) {
                    normalizedName.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));

                    if (i < words.length - 1) {
                        normalizedName.append(" ");
                        emailPrefix.append(word.charAt(0));
                    }
                }
            }

            student.name = normalizedName.toString();
            student.email = emailPrefix.toString() + "@ptit.edu.vn";
            System.out.println("Tên sau chuẩn hóa: " + student.name);
            System.out.println("Email tạo ra: " + student.email);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write(requestId.getBytes());

            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(student);
            oos.flush();

            byte[] sendData2 = baos.toByteArray();
            DatagramPacket sendPacket2 = new DatagramPacket(sendData2, sendData2.length, serverAddress, port);
            socket.send(sendPacket2);
            System.out.println("Đã gửi kết quả lên server thành công.");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("Đã đóng Socket.");
            }
        }
    }
}