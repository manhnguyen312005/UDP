import UDP.Customer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ProjectXuLyCustomer {
    public static void main(String[] args) {
        String studentCode = "B23DCDT169";
        String qCode = "DK7xMbs0";
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

            byte[] receiveData = new byte[2048];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            socket.receive(receivePacket);

            String requestId = new String(receivePacket.getData(), 0, 8);
            System.out.println("Đã nhận requestId: " + requestId);

            ByteArrayInputStream bais = new ByteArrayInputStream(receivePacket.getData(), 8, receivePacket.getLength() - 8);
            ObjectInputStream ois = new ObjectInputStream(bais);
            Customer customer = (Customer) ois.readObject();
            System.out.println("Tên gốc nhận được: " + customer.name);
            System.out.println("Ngày sinh gốc nhận được: " + customer.dayOfBirth);

            String[] nameWords = customer.name.trim().toLowerCase().split("\\s+");
            String lastName = nameWords[nameWords.length - 1].toUpperCase();

            StringBuilder normalizedName = new StringBuilder(lastName).append(",");
            StringBuilder userName = new StringBuilder();

            for (int i = 0; i < nameWords.length - 1; i++) {
                String word = nameWords[i];
                if (word.length() > 0) {
                    normalizedName.append(" ").append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
                    userName.append(word.charAt(0));
                }
            }

            userName.append(nameWords[nameWords.length - 1]);

            customer.name = normalizedName.toString();
            customer.userName = userName.toString();

            String[] dateParts = customer.dayOfBirth.trim().split("-");
            if (dateParts.length == 3) {
                String mm = dateParts[0];
                String dd = dateParts[1];
                String yyyy = dateParts[2];
                customer.dayOfBirth = dd + "/" + mm + "/" + yyyy;
            }

            System.out.println("Tên sau chuẩn hóa: " + customer.name);
            System.out.println("UserName tạo ra: " + customer.userName);
            System.out.println("Ngày sinh sau chuẩn hóa: " + customer.dayOfBirth);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write(requestId.getBytes());

            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(customer);
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