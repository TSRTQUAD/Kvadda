function trajectory = tspsolver(nodes,costmat)
% Write the problemfile problem.par
fileID = fopen('problem.par','w');
fprintf(fileID,'%s\n','PROBLEM_FILE = problem.tsp',...
    'INITIAL_TOUR_ALGORITHM = WALK','MOVE_TYPE = 5','PATCHING_C = 3',...
    'PATCHING_A = 2','TIME_LIMIT = 3','RUNS = 1',...
    'TOUR_FILE = result.tour');
fprintf(fileID,'%s\n','EOF');
fclose(fileID);

% Write the problemfile problem.tsp
dimension = length(nodes);
fileID = fopen('problem.tsp','w');
fprintf(fileID,'%s\n','NAME : problem','COMMENT : Path optimization',...
    'TYPE : TSP',['DIMENSION : ' num2str(dimension)],...
    'NODE_COORD_TYPE : TWOD_COORDS','EDGE_WEIGHT_TYPE : EXPLICIT',...
    'EDGE_WEIGHT_FORMAT : FULL_MATRIX');
fprintf(fileID,'%s\n','NODE_COORD_SECTION :');
for ii = 1:dimension
    fprintf(fileID,'%s\n',[num2str(ii) ' ' num2str(nodes(ii,1))...
        ' ' num2str(nodes(ii,2))]);
end
fprintf(fileID,'%s\n','EDGE_WEIGHT_SECTION :');
rows = size(costmat,1);
for ii = 1:rows
    fprintf(fileID,'%s',num2str(costmat(ii,:)));
    fprintf(fileID,'%s\n','');
end
fprintf(fileID,'%s\n','EOF');
fclose(fileID);

% Solve the TSP-problem
system('./LKH problem.par')

% Open the resultfile and specify the trajectory
fileID = fopen('result.tour');
tline = fgetl(fileID);
trajectoryind = zeros(dimension,1); ind = 1;
while ischar(tline)
    if all(isstrprop(tline,'digit'))
        trajectoryind(ind) = str2num(tline);
        ind = ind+1;
    end
    tline = fgetl(fileID);
end
fclose(fileID);
trajectory = nodes(trajectoryind,:);

% Delete files
delete('problem.par', 'problem.tsp', 'result.tour')
end