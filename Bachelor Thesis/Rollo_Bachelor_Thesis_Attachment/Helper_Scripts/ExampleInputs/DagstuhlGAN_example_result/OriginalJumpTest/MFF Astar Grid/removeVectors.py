import os

def delete_selected_txt_files(root_folder):
    for dirpath, dirnames, filenames in os.walk(root_folder):
        for filename in filenames:
            if filename.endswith('.txt'):
                try:
                    num = int(filename[:-4])
                    if 4 <= num <= 99:
                        file_path = os.path.join(dirpath, filename)
                        os.remove(file_path)
                        print(f"Deleted: {file_path}")
                except ValueError:
                    continue

# Replace with your actual root path
delete_selected_txt_files(r'C:\Users\kokod\Desktop\Rollo_Thesis_Attachment\Helper_Scripts\ExampleInputs\results_MechanicsGen')