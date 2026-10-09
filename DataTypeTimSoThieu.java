import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class DataTypeTimSoThieu {
    public static void main(String[] args) {
        String studentCode = "B23DCDT169"; 
        String qCode = "DJJhCzWb";
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
            if (parts.length == 3) {
                String requestId = parts[0];
                int n = Integer.parseInt(parts[1]);
                String[] aValuesStr = parts[2].split(",");

                boolean[] present = new boolean[n + 1];
                for (String valStr : aValuesStr) {
                    if (!valStr.trim().isEmpty()) {
                        int val = Integer.parseInt(valStr.trim());
                        if (val >= 1 && val <= n) {
                            present[val] = true;
                        }
                    }
                }

                List<Integer> missingValues = new ArrayList<>();
                for (int i = 1; i <= n; i++) {
                    if (!present[i]) {
                        missingValues.add(i);
                    }
                }

                StringBuilder answerBuilder = new StringBuilder(requestId).append(";");
                for (int i = 0; i < missingValues.size(); i++) {
                    answerBuilder.append(missingValues.get(i));
                    if (i < missingValues.size() - 1) {
                        answerBuilder.append(",");
                    }
                }

                String answerMessage = answerBuilder.toString();
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