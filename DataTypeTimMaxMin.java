import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class DataTypeTimMaxMin {
    public static void main(String[] args) {
        String studentCode = "B23DCDT169"; 
        String qCode = "NjyY4AJ9";
        String serverIp = "36.50.135.242";
        int port = 2207;

        DatagramSocket socket = null;

        try {
            socket = new DatagramSocket();
            InetAddress serverAddress = InetAddress.getByName(serverIp);
            socket.setSoTimeout(5000); 

            String requestMessage = ";" + studentCode + ";" + qCode;
            byte[] sendData1 = requestMessage.getBytes();
            DatagramPacket sendPacket1 = new DatagramPacket(sendData1, sendData1.length, serverAddress, port);
            socket.send(sendPacket1);
            System.out.println("Đã gửi: " + requestMessage);

            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            socket.receive(receivePacket);
            
            String responseMessage = new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();
            System.out.println("Đã nhận: " + responseMessage);

            String[] parts = responseMessage.split(";");
            if (parts.length == 2) {
                String requestId = parts[0];
                String[] numbersStr = parts[1].split(",");

                int max = Integer.MIN_VALUE;
                int min = Integer.MAX_VALUE;

                for (String numStr : numbersStr) {
                    if (!numStr.trim().isEmpty()) {
                        int num = Integer.parseInt(numStr.trim());
                        if (num > max) {
                            max = num;
                        }
                        if (num < min) {
                            min = num;
                        }
                    }
                }

                String answerMessage = requestId + ";" + max + "," + min;
                byte[] sendData2 = answerMessage.getBytes();
                DatagramPacket sendPacket2 = new DatagramPacket(sendData2, sendData2.length, serverAddress, port);
                socket.send(sendPacket2);
                System.out.println("Đã gửi lại: " + answerMessage);
            } else {
                System.out.println("Thông điệp từ server không đúng định dạng!");
            }
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