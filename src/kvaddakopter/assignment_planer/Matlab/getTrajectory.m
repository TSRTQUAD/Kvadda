function rawtrajectory = getTrajectory( tmpcostmat, nodes, startpoint )
% The TSP-solver kan only handle integers, rescale the costmatrix to fit
% each element in an 32-bit unsigned variable.
normalizationfactor = 2^16/max([max(max(tmpcostmat.lon)),max(max...
    (tmpcostmat.lat)),max(max(tmpcostmat.cross))]);

pattern = {'lon' 'lat' 'cross'};
bestsolution = inf;
for ii = 1:3
    costmat = round(normalizationfactor*tmpcostmat.(pattern{ii}));
    trajectory.(pattern{ii}) = tspsolver(nodes,costmat);
    
    % Calculate the total trajectory distance and sort out the shortest
    distance = 0;
    for kk = 2:length(trajectory.(pattern{ii}))
        distance = distance + lldistkm(trajectory...
            .(pattern{ii})(kk-1,:),trajectory.(pattern{ii})(kk,:));
    end
    % THIS IS WHERE A FUNCTION THAT EVALUATES EACH TRAJECTORY BASED ON BOTH
    % NUMBER OF TURNS AND DISTANCE SHOULD BE IMPLEMENTED.
    currentsolution = distance;
    if currentsolution < bestsolution
        besttrajectory = trajectory.(pattern{ii});
        bestsolution = currentsolution;
    end
end

% Add the startpoint to the beginning and end of the trajectory icluding
% some points inbetween to straighten up the future interpolation
rawtrajectory = getStartEndPath(startpoint, besttrajectory);

end