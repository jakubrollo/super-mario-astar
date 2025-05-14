import json

# Define the mapping from symbol to identity
symbol_to_identity = {
    'X': '0',  # solid/Ground
    'S': '1',  # breakable
    '-': '2',  # empty (passable)
    '@': '3',  # full question block
    'D': '4',  # empty question block
    'g': '5',  # enemy
    't': '6',  # top-left pipe
    'T': '7',  # pipe_flower
    'M': '2',  # mario
    'F': '2',
    '!': '3',
    '#': '0',
    'U': '3',
    '2': '1',
    '1': '1',
    'D': '4',
    'o': '2',
    '*': '2',
    '|': '2',
    '%': '1',
    'C': '3',
    'L': '3',
    'G': '5',
    'r': '5',
    'R': '5',
    'k': '5',
    'K': '5',
    'y': '5',
    'Y': '5'
}

# Identity to symbol
identity_to_symbol = {
    '0': 'X',  # solid/ground
    '1': 'S',  # breakable
    '2': '-',  # empty
    '3': '@',  # full question block
    '4': 'D',  # empty question block
    '5': 'g',  # enemy
    '6': 't',  # top-left pipe
    '7': 't',  # top-right pipe
    '8': 't',  # right pipe
    '9': 't'   # left pipe
}

def replace_symbols_with_identities(level_data):
    return ''.join(symbol_to_identity.get(char, char) for char in level_data)

def replace_identities_with_symbols(level_data):
    return ''.join(identity_to_symbol.get(char, char) for char in level_data)


def process_and_write_to_file(input_filename=r'ExampleInputs\marioInput.txt', output_filename=r'ExampleInputs\marioOutput.txt'):
    """
    Processes the input file by reading the contents, removing brackets, 
    and mapping symbols to identities, then writes the output to a new file
    """
    with open(input_filename, 'r') as file:
        input_string = file.read()
    
    input_string = input_string.replace('\n', "").replace(" ", "")
    input_string = input_string.strip('[]')
    rows = input_string.split('],[')
    rows_without_dash = []
    for row in rows:
        rows_without_dash.append(row.replace(",", ""))

    identities_rows = []
    for row in rows_without_dash:
        identities_rows.append(replace_identities_with_symbols(row))

    with open(output_filename, 'w') as file:
        for row in identities_rows:
            file.write(row + '\n')

def generate_frames(level_data, window_width=28, window_height=14):
    """Generate frames by sliding a window over the level"""
    frames = []
    
    for i in range(len(level_data) - window_height + 1):
        for j in range(len(level_data[0]) - window_width + 1):
            frame = [list(map(int, row[j:j + window_width])) for row in level_data[i:i + window_height]]
            frames.append(frame)
    
    return frames

def level_to_frames(input_filename=r'ExampleInputs\marioInput2.txt'):
    with open(input_filename, 'r') as file:
        level = file.read()
    level_with_identities = replace_symbols_with_identities(level)
    level_lines = level_with_identities.splitlines()
    frames = generate_frames(level_lines)
    
    return frames

def save_frames_to_json(frames, filename=r'ExampleInputs\example.json'):
    with open(filename, 'w') as json_file:
        json.dump(frames, json_file, indent=4)


"""
with open('marioInput2.txt', 'r') as file:
    level = file.read()
level_with_identities = replace_symbols_with_identities(level)
level = replace_identities_with_symbols(level_with_identities)

with open("rewritten_level.txt", 'w') as file:
    file.write(level)
"""

process_and_write_to_file()
frames = level_to_frames()
save_frames_to_json(frames)