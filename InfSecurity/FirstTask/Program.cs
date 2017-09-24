using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Management;
using System.Runtime.InteropServices;
using System.Text;

namespace FirstTask
{
    class Drive
    {
        public string Name;
        public string Model;
        public string FirmwareRevision;
        public string SerialNumber;
        public long Size;

        public override string ToString()
        {
            return Name + " " + Model + " " + FirmwareRevision + " " + Size;
        }
    }
    
    internal class Program
    {
        static long ONE_GB = 1073741824;
        
        const int PROCESS_WM_READ = 0x0010;
        const int PROCESS_VM_WRITE = 0x0020;
        const int PROCESS_VM_OPERATION = 0x0008;

        [DllImport("kernel32.dll")]
        public static extern IntPtr OpenProcess(int dwDesiredAccess, bool bInheritHandle, int dwProcessId);

        [DllImport("kernel32.dll")]
        public static extern bool ReadProcessMemory(int hProcess, 
            int lpBaseAddress, byte[] lpBuffer, int dwSize, ref int lpNumberOfBytesRead);
        
        [DllImport("kernel32.dll", SetLastError = true)]
        static extern bool WriteProcessMemory(int hProcess, int lpBaseAddress, 
            byte[] lpBuffer, int dwSize, ref int lpNumberOfBytesWritten);
        
        public static void Main(string[] args)
        {
            try
            {
                List<Drive> drives = new List<Drive>();
                foreach (ManagementObject drive in new ManagementObjectSearcher("SELECT * FROM Win32_DiskDrive").Get())
                {
                    var d = new Drive
                    {
                        Model = (string) drive["Model"],
                        SerialNumber = (string) drive["SerialNumber"],
                        FirmwareRevision = (string) drive["FirmwareRevision"],
                        Size = long.Parse(drive["Size"].ToString()) / ONE_GB
                    };

                    foreach (ManagementObject partition in new ManagementObjectSearcher(
                        "ASSOCIATORS OF {Win32_DiskDrive.DeviceID='" + drive["DeviceID"]
                        + "'} WHERE AssocClass=Win32_DiskDriveToDiskPartition").Get())
                    {
                        foreach (ManagementObject disk in new ManagementObjectSearcher(
                            "ASSOCIATORS OF {Win32_DiskPartition.DeviceID='" + partition["DeviceID"]
                            + "'} WHERE AssocClass=Win32_LogicalDiskToPartition").Get())
                        {
                            d.Name = (string) disk["Name"];
                        }
                    }
                    drives.Add(d);
                }

                foreach (var d in drives)
                {
                    Console.WriteLine(d.ToString());
                }
                
               
             
                string root = Path.GetPathRoot(System.Reflection.Assembly.GetEntryAssembly().Location);
                Console.WriteLine(root);

                int id = Process.GetCurrentProcess().Id;
                
                 
                
                Console.WriteLine(Process.GetCurrentProcess().Id);
            }
            catch (Exception e)
            {
               Console.WriteLine(e.Message);
            }

            /*Process process = Process.GetProcessesByName("Microsoft.Photos")[0];
            Console.WriteLine(process);
            
            IntPtr processHandle = OpenProcess(PROCESS_WM_READ, false, process.Id); 
            
            int bytesWritten = 0;
            byte[] buffer = Encoding.Unicode.GetBytes("It works!\0");
            
          // WriteProcessMemory((int)processHandle, 0xdd, buffer, buffer.Length, ref bytesWritten);

            int bytesRead = 0;
            buffer = new byte[24];
            ReadProcessMemory((int)processHandle, 0xee, buffer, buffer.Length, ref bytesRead);

            Console.WriteLine(Encoding.Unicode.GetString(buffer) + " (" + bytesRead + "bytes)");*/
        }
    }
}